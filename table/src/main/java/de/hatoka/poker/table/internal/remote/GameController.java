package de.hatoka.poker.table.internal.remote;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.game.Dealer;
import de.hatoka.poker.table.capi.event.game.DealerFactory;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import de.hatoka.poker.table.internal.event.GameInfo;
import de.hatoka.poker.table.internal.event.PlayerActions;

@RestController
@RequestMapping(value = GameController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class GameController
{
    private static class GameRecord
    {
        private final GameBO game;
        private final SeatBO seat;
        private final PlayerActions player;

        private GameRecord(GameBO gameBO, SeatBO seatBO, PlayerActions player)
        {
            this.game = gameBO;
            this.seat = seatBO;
            this.player = player;
        }
    }

    public static final String PATH_ROOT = "/tables/{tableid}/games/{gameno}";
    public static final String PATH_SUB_TABLE = "/{tableid}";
    public static final String PATH_VAR_TABLE = "tableid";
    public static final String PATH_VAR_GAMENO = "gameno";
    public static final String PATH_TABLE = PATH_ROOT + PATH_SUB_TABLE;
    public static final String PATH_SUB_ACTION = "/action";
    public static final String MATRIX_SEATPOS = "seat";

    @Autowired
    private TableBORepository tableRepository;
    @Autowired
    private GameBO2RO gameBO2RO;
    @Autowired
    private DealerFactory dealerFactory;
    @Autowired
    private PlayerFactory playerFactory;

    @Autowired
    private RestControllerErrorSupport errorSupport;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public GameRO get(@PathVariable(PATH_VAR_TABLE) String tableID, @MatrixVariable(pathVar = PATH_VAR_GAMENO) Map<String, String> matrixVars)
    {
        GameRecord record = getGameRecord(tableID, matrixVars.get(MATRIX_SEATPOS));
        Dealer dealer = dealerFactory.get(record.game.getTable());
        dealer.doWhatEverYouNeed();
        return gameBO2RO.apply(record.game, record.seat);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = PATH_SUB_ACTION, consumes = { APPLICATION_JSON_VALUE })
    public GameRO playerAction(@PathVariable(PATH_VAR_TABLE) String tableID, @MatrixVariable(pathVar = PATH_VAR_GAMENO) Map<String, String> matrixVars, @RequestBody PlayerGameActionRO actionInfo)
    {
        GameRecord record = getGameRecord(tableID, matrixVars.get(MATRIX_SEATPOS));
        GameInfo info = new GameInfo(record.game);
        if (info.getSeatHasAction().isPresent())
        {
            LoggerFactory.getLogger(getClass()).debug("player {} has action.", info.getSeatHasAction().get().getGlobalRef());
            if (!info.getSeatHasAction().get().equals(record.seat.getRef()))
            {
                errorSupport.throwPreconditionFailedException("noaction.player", info.getSeatHasAction().toString());
            }
            switch(actionInfo.getAction())
            {
                case fold:
                    record.player.fold();
                    break;
                case check:
                    record.player.check();
                    break;
                case call:
                    record.player.call();
                    break;
                case raise:
                case bet:
                    record.player.betTo(actionInfo.getBetTo());
                    break;
                case allin:
                    record.player.allIn();
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected value: " + actionInfo.getAction());
            }
        }
        if (info.hasDealerAction())
        {
            LoggerFactory.getLogger(getClass()).debug("dealer has action.");
            Dealer dealer = dealerFactory.get(record.game.getTable());
            dealer.doWhatEverYouNeed();
        }
        return gameBO2RO.apply(record.game, record.seat);
        
    }
    
    private GameRecord getGameRecord(String tableID, String seaPos)
    {
        TableRef tableRef = TableRef.localRef(tableID);
        Optional<TableBO> tableOpt = tableRepository.findTable(tableRef);
        if (!tableOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.table", tableRef.toString());
        }
        Optional<GameBO> gameOpt = tableOpt.get().getCurrentGame();
        if (!gameOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.game", tableRef.toString());
        }
        SeatRef seatRef = SeatRef.localRef(tableRef, Integer.valueOf(seaPos));
        Optional<SeatBO> seatOpt = tableOpt.get().getSeatRepository().findSeat(seatRef);
        if (!seatOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.seat", seatRef.toString());
        }
        PlayerActions player = playerFactory.get(seatOpt.get());
        return new GameRecord(gameOpt.get(), seatOpt.get(), player);
    }
}
