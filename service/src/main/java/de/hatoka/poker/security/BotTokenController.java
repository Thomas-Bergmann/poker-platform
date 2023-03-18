package de.hatoka.poker.security;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.oidc.capi.business.TokenUtils;
import de.hatoka.oidc.capi.remote.TokenResponse;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.remote.OAuthBotAuthenticationRO;
import de.hatoka.poker.remote.OAuthRefreshRO;

@RestController
@RequestMapping(value = BotTokenController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class BotTokenController
{
    public static final String PATH_ROOT = "/auth/bots";
    public static final String PATH_SUB_BOT_TOKEN = "/token";
    public static final String PATH_SUB_BOT_REFRESH = "/refresh";

    @Autowired
    private PlayerBORepository playerRepository;
    @Autowired
    private RestControllerErrorSupport errorSupport;
    @Autowired
    private TokenUtils tokenUtils;

    @PostMapping(value = PATH_SUB_BOT_TOKEN, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse createTokenForBot(@RequestBody OAuthBotAuthenticationRO input)
    {
        PlayerRef playerRef = PlayerRef.globalRef(input.getBotRef());
        Optional<PlayerBO> playerOpt = playerRepository.findPlayer(playerRef);
        if (!playerOpt.isPresent())
        {
            errorSupport.throwUnauthorizedException("authfailed.bot", playerRef.toString());
        }
        PlayerBO bot = playerOpt.get();
        if (!bot.getApiKey().equals(input.getApiKey()))
        {
            errorSupport.throwUnauthorizedException("authfailed.bot", playerRef.toString());
        }
        return tokenUtils.createTokenForSubject(bot.getRef().getGlobalRef());
    }

    @PostMapping(value = PATH_SUB_BOT_REFRESH, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse createTokenFromRefresh(@RequestBody OAuthRefreshRO input)
    {
        return tokenUtils.createTokenFromRefreshToken(input.getRefreshToken());
    }
}
