package de.hatoka.poker.bot.remote.client;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.hatoka.poker.remote.OAuthTokenResponse;
import de.hatoka.poker.remote.OAuthBotAuthenticationRO;
import de.hatoka.poker.remote.GameRO;
import de.hatoka.poker.remote.PlayerGameActionRO;
import de.hatoka.poker.remote.SeatRO;
import de.hatoka.poker.remote.TableRO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PokerServiceClientImpl implements PokerServiceClient
{
    private static final String PATH_TOKEN = "/auth/bots/token";
    private static final String PATH_TABLES = "/tables";
    private static final String PATH_SEATS = "/tables/{table}/seats";

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private final String serviceURI;
    private final String botRef;
    private final String botKey;

    private GameRO lastGame = null;
    private long nextUpdate = 0L;
    private String gameSeatURI = null;

    public PokerServiceClientImpl(String serviceURI, String botRef, String botKey)
    {
        this.serviceURI = serviceURI;
        this.botRef = botRef;
        this.botKey = botKey;
    }

    @Override
    public List<TableRO> getTables()
    {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return Arrays.asList(restTemplate
                                         .exchange(serviceURI + PATH_TABLES, HttpMethod.GET,
                                                         new HttpEntity<>(getHeaders()), TableRO[].class)
                                         .getBody());
    }

    @Override
    public GameRO getGame(SeatRO seat)
    {
        if (lastGame != null && nextUpdate < System.currentTimeMillis())
        {
            return lastGame;
        }
        if (gameSeatURI != null)
        {
            return getGame(gameSeatURI);
        }
        return getGame(getSeatURI(seat));
    }

    private GameRO getGame(String seatGameURI)
    {
        RestTemplate restTemplate = restTemplateBuilder.build();
        GameRO result = restTemplate.exchange(seatGameURI, HttpMethod.GET, new HttpEntity<>(getHeaders()), GameRO.class)
                                    .getBody();
        updateGame(seatGameURI, result);
        return result;
    }

    @Override
    public List<SeatRO> getSeats(TableRO table)
    {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return Arrays.asList(restTemplate.exchange(serviceURI + PATH_SEATS, HttpMethod.GET,
                        new HttpEntity<>(getHeaders()), SeatRO[].class, table.getRefLocal()).getBody());
    }

    @Override
    public void doAction(SeatRO seat, PlayerGameActionRO action)
    {
        RestTemplate restTemplate = restTemplateBuilder.build();
        String seatGameURI = getSeatURI(seat);
        GameRO result = restTemplate.exchange(seatGameURI + ";/action", HttpMethod.POST,
                        new HttpEntity<>(action, getHeaders()), GameRO.class).getBody();
        updateGame(seatGameURI, result);
    }

    private String getSeatURI(SeatRO seat)
    {
        return serviceURI + seat.getInfo().getTableResourceURI() + "/games/current" + ";seat=" + seat.getRefLocal();
    }

    private String getBearerToken()
    {
        OAuthBotAuthenticationRO authData = new OAuthBotAuthenticationRO();
        authData.setBotRef(botRef);
        authData.setApiKey(botKey);
        RestTemplate restTemplate = restTemplateBuilder.build();
        OAuthTokenResponse result = restTemplate.exchange(serviceURI + PATH_TOKEN, HttpMethod.POST,
                        new HttpEntity<>(authData, getHeaders()), OAuthTokenResponse.class).getBody();
        return result.getAccessToken();
    }

    private HttpHeaders getHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getBearerToken());
        return headers;
    }

    @Override
    public Optional<SeatRO> getBotSeat(List<SeatRO> seats)
    {
        return seats.stream().filter(s -> botRef.equals(s.getInfo().getBotRef())).findAny();
    }

    private void updateGame(String gameSeatURI, GameRO game)
    {
        this.gameSeatURI = gameSeatURI;
        this.lastGame = game;
        this.nextUpdate = System.currentTimeMillis() + 5000;
    }

}
