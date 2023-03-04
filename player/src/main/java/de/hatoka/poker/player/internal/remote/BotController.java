package de.hatoka.poker.player.internal.remote;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.player.capi.remote.BotDataRO;
import de.hatoka.poker.player.capi.remote.BotRO;
import de.hatoka.user.capi.business.UserRef;

@RestController
@RequestMapping(value = BotController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class BotController
{
    public static final String PATH_ROOT = "/players/{playerid}/bots";
    public static final String PATH_SUB_BOT = "/{botid}";
    public static final String PATH_VAR_PLAYERID= "playerid";
    public static final String PATH_VAR_BOTID= "botid";
    public static final String PATH_BOT = PATH_ROOT + PATH_SUB_BOT;
    
    @Autowired
    private PlayerBORepository playerRepository;
    @Autowired
    private PlayerBO2BotRO playerBO2BotRO;

    @Autowired
    private RestControllerErrorSupport errorSupport;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BotRO> getBots(@PathVariable(PATH_VAR_PLAYERID) String playerID)
    {
        PlayerRef playerRef = PlayerRef.humanRef(playerID);
        return playerBO2BotRO.apply(playerRepository.getBots(playerRef.getUserRef()));
    }

    @PutMapping(value = PATH_SUB_BOT, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public void createBot(@PathVariable(PATH_VAR_PLAYERID) String playerID, @PathVariable(PATH_VAR_BOTID) String botID, @RequestBody BotDataRO input)
    {
        PlayerRef playerRef = PlayerRef.botRef(playerID, botID);
        Optional<PlayerBO> playerOpt = playerRepository.findPlayer(playerRef);
        if (playerOpt.isPresent())
        {
            errorSupport.throwPreconditionFailedException("found.player", playerRef.toString());
        }
        UserRef userRef = UserRef.globalRef(input.getOwnerRef());
        playerRepository.createBotPlayer(userRef, botID);
    }

    @GetMapping(PATH_SUB_BOT)
    @ResponseStatus(HttpStatus.OK)
    public BotRO getBot(@PathVariable(PATH_VAR_PLAYERID) String playerID, @PathVariable(PATH_VAR_BOTID) String botID)
    {
        PlayerRef playerRef = PlayerRef.botRef(playerID, botID);
        Optional<PlayerBO> playerOpt = playerRepository.findPlayer(playerRef);
        if (!playerOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.player", playerRef.toString());
        }
        return playerBO2BotRO.apply(playerOpt.get());
    }

    @DeleteMapping(PATH_SUB_BOT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deletePlayer(@PathVariable(PATH_VAR_PLAYERID) String playerID, @PathVariable(PATH_VAR_BOTID) String botID)
    {
        PlayerRef playerRef = PlayerRef.botRef(playerID, botID);
        Optional<PlayerBO> playerOpt = playerRepository.findPlayer(playerRef);
        if (playerOpt.isPresent())
        {
            playerOpt.get().remove();
        }
    }
}
