package de.hatoka.poker.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import de.hatoka.poker.bot.remote.client.PokerClientFactory;
import de.hatoka.poker.bot.remote.client.PokerServiceClient;
import de.hatoka.poker.bot.strategy.PokerStrategy;
import de.hatoka.poker.bot.strategy.PokerStrategyFactory;

@SpringBootApplication
@ComponentScan
public class BotApplication implements CommandLineRunner
{
    private final int POS_SERVICE_URI = 0;
    private final int POS_BOT_REF = 1;
    private final int POS_BOT_KEY = 2;

    private static Logger LOGGER = LoggerFactory.getLogger(BotApplication.class);

    public static void main(String[] args)
    {
        SpringApplication.run(BotApplication.class, args);
    }

    @Override
    public void run(String... args)
    {
        try
        {
            if (args.length > POS_BOT_KEY)
            {
                run(args[POS_SERVICE_URI], args[POS_BOT_REF], args[POS_BOT_KEY]);
            }
            else
            {
                printCommands();
                System.exit(1);
            }
        }
        catch(Exception e)
        {
            LOGGER.error("Something goes wrong.", e);
            System.exit(1);
        }
        System.exit(0);
    }

    private static void printCommands()
    {
        System.out.printf("* 1 service uri\n");
        System.out.printf("* 2 bot ref\n");
        System.out.printf("* 3 bot key\n");
        System.out.printf("\n");
    }
    
    private final PokerClientFactory clientFactory; 
    private final PokerStrategyFactory strategyFactory;

    public BotApplication(PokerClientFactory clientFactory, PokerStrategyFactory strategyFactory)
    {
        this.clientFactory = clientFactory;
        this.strategyFactory = strategyFactory;
    }
    
    public void run(String serviceURI, String botRef, String botKey)
    {
        PokerServiceClient client = clientFactory.create(serviceURI, botRef, botKey);
        PokerStrategy strategy = strategyFactory.create(client);
        strategy.run();
    }
}
