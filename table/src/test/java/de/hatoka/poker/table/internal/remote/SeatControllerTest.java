package de.hatoka.poker.table.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.common.capi.rest.test.TestSecurityConfiguration;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.remote.SeatDataRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.table.TableTestApplication;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TableTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { TableTestConfiguration.class, TestSecurityConfiguration.class })
@ActiveProfiles("test")
public class SeatControllerTest
{
    private static final TableRef TABLE_REF_ONE = TableRef.localRef("table-one");
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");
    private static final PlayerRef PLAYER_COMPUTE_REF = PlayerRef.botRef(OWNER_REF, "compute-one");
    private static final PlayerRef PLAYER_HUMAN_REF = PlayerRef.humanRef(OWNER_REF);

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TableBORepository tableRepository;
    @Autowired
    private PlayerBORepository playerRepository;

    @BeforeEach 
    public void createTable()
    {
        tableRepository.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        playerRepository.createHumanPlayer(OWNER_REF);
        playerRepository.createBotPlayer(OWNER_REF, PLAYER_COMPUTE_REF.getName());
    }

    @AfterEach
    public void deleteRepo()
    {
        tableRepository.clear();
        playerRepository.clear();
    }

    @Test
    public void createSeatAndDelete()
    {
        SeatDataRO data = new SeatDataRO();
        data.setPlayerRef(PLAYER_COMPUTE_REF.getGlobalRef());
        SeatRO seat1 = postSeat(TABLE_REF_ONE, data);
        assertEquals(PLAYER_COMPUTE_REF.getGlobalRef(), seat1.getData().getPlayerRef());

        data.setPlayerRef(PLAYER_HUMAN_REF.getGlobalRef());
        SeatRO seat2 = postSeat(TABLE_REF_ONE, data);
        assertEquals(PLAYER_HUMAN_REF.getGlobalRef(), seat2.getData().getPlayerRef());

        List<SeatRO> seats = getSeats();
        assertEquals(2, seats.size());
        deleteSeat(SeatRef.globalRef(seats.get(1).getRefGlobal()));
        deleteSeat(SeatRef.globalRef(seats.get(0).getRefGlobal()));
    }

    @Test
    public void createRebuy()
    {
        SeatDataRO data = new SeatDataRO();
        data.setPlayerRef(PLAYER_COMPUTE_REF.getGlobalRef());
        data.setCoinsOnSeat(200);
        SeatRO seat1 = postSeat(TABLE_REF_ONE, data);
        assertEquals(PLAYER_COMPUTE_REF.getGlobalRef(), seat1.getData().getPlayerRef());

        data.setPlayerRef(PLAYER_HUMAN_REF.getGlobalRef());
        SeatRO seat2 = postSeat(TABLE_REF_ONE, data);
        assertEquals(PLAYER_HUMAN_REF.getGlobalRef(), seat2.getData().getPlayerRef());
        // dealer started game
        List<SeatRO> seats = getSeats();
        assertEquals(200, seats.stream().filter(s -> seat1.getRefGlobal().equals(s.getRefGlobal())).findAny().get().getData().getCoinsOnSeat());
        assertEquals(200, seats.stream().filter(s -> seat2.getRefGlobal().equals(s.getRefGlobal())).findAny().get().getData().getCoinsOnSeat());

        SeatDataRO patch = new SeatDataRO();
        patch.setCoinsOnSeat(300);
        // patch coins means 
        patchSeat(SeatRef.globalRef(seat2.getRefGlobal()), patch);
        seats = getSeats();
        assertEquals(2, seats.size());
        // seat 1 still 200 game not finished
        assertEquals(200, seats.stream().filter(s -> seat1.getRefGlobal().equals(s.getRefGlobal())).findAny().get().getData().getCoinsOnSeat());
        // seat 2 has 300 after patch (doesn't need to fold because of separation of on seat coins
        assertEquals(300, seats.stream().filter(s -> seat2.getRefGlobal().equals(s.getRefGlobal())).findAny().get().getData().getCoinsOnSeat());

        deleteSeat(SeatRef.globalRef(seats.get(1).getRefGlobal()));
        deleteSeat(SeatRef.globalRef(seats.get(0).getRefGlobal()));
    }

    private List<SeatRO> getSeats()
    {
        Map<String, String> urlParams = createURIParameter(TABLE_REF_ONE);
        return Arrays.asList(this.restTemplate.getForObject(SeatController.PATH_ROOT, SeatRO[].class, urlParams));
    }

    private Map<String, String> createURIParameter(TableRef ref)
    {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(SeatController.PATH_VAR_TABLE, ref.getLocalRef());
        return urlParams;
    }

    private Map<String, String> createURIParameter(SeatRef ref)
    {
        Map<String, String> urlParams = createURIParameter(ref.getTableRef());
        urlParams.put(SeatController.PATH_VAR_SEATID, ref.getLocalRef());
        return urlParams;
    }

    private SeatRO postSeat(TableRef ref, SeatDataRO data)
    {
        return this.restTemplate.postForObject(SeatController.PATH_ROOT + SeatController.PATH_SUB_CREATE_SEAT, data, SeatRO.class, createURIParameter(ref));
    }

    private SeatRO patchSeat(SeatRef ref, SeatDataRO data)
    {
        return this.restTemplate.patchForObject(SeatController.PATH_SEAT, data, SeatRO.class, createURIParameter(ref));
    }

    private void deleteSeat(SeatRef ref)
    {
        this.restTemplate.delete(SeatController.PATH_SEAT, createURIParameter(ref));
    }
}
