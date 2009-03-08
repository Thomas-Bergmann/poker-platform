package de.hatoka.poker.table.internal.remote;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.remote.SeatDataRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.game.Dealer;
import de.hatoka.poker.table.capi.event.game.DealerFactory;

@RestController
@RequestMapping(value = SeatController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class SeatController
{
    public static final String PATH_ROOT = "/tables/{tableid}/seats";
    public static final String PATH_SUB_CREATE_SEAT = "/joinTable";
    public static final String PATH_SUB_SEAT = "/{seatid}";
    public static final String PATH_VAR_TABLE = "tableid";
    public static final String PATH_VAR_SEATID= "seatid";
    public static final String PATH_SEAT = PATH_ROOT + PATH_SUB_SEAT;

    @Autowired
    private TableBORepository tableRepository;
    @Autowired
    private PlayerBORepository playerRepository;
    @Autowired
    private SeatBO2RO seatBO2RO;
    @Autowired
    private DealerFactory dealerFactory;

    @Autowired
    private RestControllerErrorSupport errorSupport;

    private SeatBORepository getSeatRepo(String tableID)
    {
        TableRef tableRef = TableRef.localRef(tableID);
        Optional<TableBO> tableOpt = tableRepository.findTable(tableRef);
        if (!tableOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.table", tableRef.toString());
        }
        return tableOpt.get().getSeatRepository();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<SeatRO> getSeats(@PathVariable(PATH_VAR_TABLE) String tableID)
    {
        SeatBORepository seatRepository = getSeatRepo(tableID);
        seatRepository.clearEmptySeats();
        Collection<SeatBO> seats = seatRepository.getSeats();
        return seatBO2RO.apply(seats);
    }

    @PostMapping(value = PATH_SUB_CREATE_SEAT, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public SeatRO createSeat(@PathVariable(PATH_VAR_TABLE) String tableID, @RequestBody SeatDataRO input)
    {
        SeatBORepository seatRepository = getSeatRepo(tableID);
        if (input.getPlayerRef() == null)
        {
            errorSupport.throwNotFoundException("notfound.player", input.getPlayerRef());
        }
        PlayerRef playerRef = PlayerRef.globalRef(input.getPlayerRef());
        Optional<PlayerBO> playerOpt = playerRepository.findPlayer(playerRef);
        if (!playerOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.player", input.getPlayerRef());
        }
        SeatBO seat = seatRepository.createSeat();
        seat.join(playerOpt.get(), input.getCoinsOnSeat());
        Dealer dealer = dealerFactory.get(seat.getTable());
        dealer.doWhatEverYouNeed();
        return seatBO2RO.apply(seat);
    }

    @GetMapping(PATH_SUB_SEAT)
    @ResponseStatus(HttpStatus.OK)
    public SeatRO getSeat(@PathVariable(PATH_VAR_TABLE) String tableID, @PathVariable(PATH_VAR_SEATID) String seatID)
    {
        SeatBORepository seatRepository = getSeatRepo(tableID);
        Optional<SeatBO> seatOpt = seatRepository.findSeat(Integer.valueOf(seatID));
        if (!seatOpt.isPresent())
        {
            TableRef tableRef = TableRef.localRef(tableID);
            errorSupport.throwNotFoundException("notfound.seat", SeatRef.localRef(tableRef, Integer.valueOf(seatID)).toString());
        }
        return seatBO2RO.apply(seatOpt.get());
    }

    @PatchMapping(value = PATH_SUB_SEAT, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public SeatRO updateSeat(@PathVariable(PATH_VAR_TABLE) String tableID, @PathVariable(PATH_VAR_SEATID) String seatID, @RequestBody SeatDataRO input)
    {
        SeatBORepository seatRepository = getSeatRepo(tableID);
        Optional<SeatBO> seatOpt = seatRepository.findSeat(Integer.valueOf(seatID));
        if (!seatOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.seat", SeatRef.localRef(seatRepository.getTableRef(), Integer.valueOf(seatID)).toString());
        }
        SeatBO seat = seatOpt.get();
        if (input.getCoinsOnSeat() != null)
        {
            seat.buyin(input.getCoinsOnSeat() - seat.getAmountOfCoinsOnSeat());
        }
        if (input.isSittingOut() != null)
        {
            seat.setSittingOut(input.isSittingOut());
        }
        Dealer dealer = dealerFactory.get(seatRepository.getTable());
        dealer.doWhatEverYouNeed();
        return seatBO2RO.apply(seat);
    }

    @DeleteMapping(PATH_SUB_SEAT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteSeat(@PathVariable(PATH_VAR_TABLE) String tableID, @PathVariable(PATH_VAR_SEATID) String seatID)
    {
        SeatBORepository seatRepository = getSeatRepo(tableID);
        Optional<SeatBO> seatOpt = seatRepository.findSeat(Integer.valueOf(seatID));
        if (seatOpt.isPresent())
        {
            seatOpt.get().remove();
        }
    }
}
