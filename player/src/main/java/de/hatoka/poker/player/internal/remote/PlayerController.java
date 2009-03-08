package de.hatoka.poker.player.internal.remote;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.remote.PlayerRO;

@RestController
@RequestMapping(value = PlayerController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class PlayerController
{
    public static final String PATH_ROOT = "/players";
    public static final String PATH_SUB_PLAYER = "/{playerid}";
    public static final String PATH_VAR_PLAYERID= "playerid";
    public static final String PATH_PLAYER = PATH_ROOT + PATH_SUB_PLAYER;
    
    @Autowired
    private PlayerBORepository playerRepository;
    @Autowired
    private PlayerBO2RO playerBO2RO;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<PlayerRO> getPlayers()
    {
        return playerBO2RO.apply(playerRepository.getAllHumanPlayers());
    }
}
