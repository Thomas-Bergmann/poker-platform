package de.hatoka.poker.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Pot
{
    static class SidePod
    {
        // these seats have rights to the pot
        @JsonProperty("seats")
        private final List<String> seats;

        // amount of coins in pot
        @JsonProperty("coins")
        private final int amountCoins;

        /**
         * Default constructor json mapper
         */
        public SidePod()
        {
            this.amountCoins = 0;
            this.seats = new ArrayList<>();
        }

        private SidePod(Set<String> givenSeats, int coins)
        {
            this.amountCoins = coins;
            List<String> seats = new ArrayList<>();
            seats.addAll(givenSeats);
            this.seats = seats;
        }

        @Override
        public String toString()
        {
            return "[seats=" + seats + ", amountCoins=" + amountCoins + "]";
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(amountCoins, seats);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SidePod other = (SidePod)obj;
            return amountCoins == other.amountCoins && Objects.equals(seats, other.seats);
        }
    }

    public static String serialize(Pot pot)
    {
        return pot.toString();
    }

    public static Pot deserialize(String potAsString)
    {
        if (potAsString == null)
        {
            return null;
        }
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(potAsString, Pot.class);
        }
        catch(JsonProcessingException e)
        {
            throw new IllegalStateException("Can't convert pot", e);
        }
    }

    public static Pot generate()
    {
        return new Pot();
    }

    @JsonProperty("sub-pods")
    private final List<SidePod> pods = new ArrayList<>();

    public void addToPot(Map<String, Integer> coinsOfEachSeat)
    {
        Set<String> seatsOfNewMainPot = coinsOfEachSeat.keySet();
        int minAmount = getMinAmount(coinsOfEachSeat.values());
        int maxAmount = getMaxAmount(coinsOfEachSeat.values());
        if (minAmount < maxAmount)
        {
            // split in to two hashes one for minAmount and the second for more than minAmount
            Map<String, Integer> minAmountPod = new HashMap<>();
            Map<String, Integer> moreAmountPod = new HashMap<>();
            for (Entry<String, Integer> entry : coinsOfEachSeat.entrySet())
            {
                minAmountPod.put(entry.getKey(), minAmount);
                int diff = entry.getValue() - minAmount;
                if (diff > 0)
                {
                    moreAmountPod.put(entry.getKey(), diff);
                }
            }
            addToPot(minAmountPod);
            addToPot(moreAmountPod);
        }
        else
        {
            // all in one pot
            int sumAmount = getSumAmount(coinsOfEachSeat.values());
            if (sumAmount > 0)
            {
                pods.add(new SidePod(seatsOfNewMainPot, sumAmount));
            }
        }
    }

    private int getMinAmount(Collection<Integer> coinsOfEachSeat)
    {
        return coinsOfEachSeat.stream().mapToInt(v -> v).min().orElse(0);
    }

    private int getMaxAmount(Collection<Integer> coinsOfEachSeat)
    {
        return coinsOfEachSeat.stream().mapToInt(v -> v).max().orElse(0);
    }

    private int getSumAmount(Collection<Integer> coinsOfEachSeat)
    {
        return coinsOfEachSeat.stream().mapToInt(v -> v).sum();
    }

    /**
     * @param winners sorted list of player first is winner (no split pot support)
     * @return
     */
    public Map<String, Integer> showdownWithoutSplitPot(List<String> places)
    {
        Map<String, Integer> result = new HashMap<>();
        Map<SidePod, Integer> availableAmount = new HashMap<>();
        pods.forEach(c -> availableAmount.put(c, c.amountCoins));
        for(String podWinner : places)
        {
            for(SidePod pod : pods)
            {
                int coins = availableAmount.get(pod);
                if (coins > 0 && pod.seats.contains(podWinner))
                {
                    Integer current = result.computeIfAbsent(podWinner, (k) -> { return 0; });
                    result.put(podWinner, current + coins);
                    availableAmount.put(pod, 0);
                }
            }
        }
        return result;
    }

    /**
     * @param winners sorted list of player first are winners (split pot support)
     * @return
     */
    public Map<String, Integer> showdown(List<List<String>> places)
    {
        Map<String, Integer> result = new HashMap<>();
        Map<SidePod, Integer> availableAmount = new HashMap<>();
        pods.forEach(c -> availableAmount.put(c, c.amountCoins));
        for(List<String> currentPlace : places)
        {
            for(SidePod pod : pods)
            {
                int coins = availableAmount.get(pod);
                if (coins > 0)
                {
                    List<String> podWinners = atBoth(pod.seats, currentPlace);
                    if (!podWinners.isEmpty())
                    {
                        int each = coins / podWinners.size();
                        for(String podWinner : podWinners)
                        {
                            Integer current = result.computeIfAbsent(podWinner, (k) -> { return 0; });
                            result.put(podWinner, current + each);
                        }
                        availableAmount.put(pod, 0);
                    }
                }
            }
        }
        return result;
    }

    private <T> List<T> atBoth(List<T> one, List<T>two)
    {
        return one.stream().filter(e -> two.contains(e)).toList();
    }

    @JsonIgnore
    public int getCoinCount()
    {
        return pods.stream().mapToInt(p -> p.amountCoins).sum();
    }

    @JsonIgnore
    public List<Integer> getCoinsPerPod()
    {
        return pods.stream().map(p -> p.amountCoins).toList();
    }

    @Override
    @JsonIgnore
    public int hashCode()
    {
        return Objects.hash(pods);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Pot other = (Pot)obj;
        return Objects.equals(pods, other.pods);
    }

    @Override
    @JsonIgnore
    public String toString()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }
        catch(JsonProcessingException e)
        {
            return "[" + pods + "]";
        }
    }

    public Map<String, Integer> getCoinsOfPlayer()
    {
        Map<String, Integer> result = new HashMap<>();
        for(SidePod pod : pods)
        {
            int coins = pod.amountCoins / pod.seats.size();
            pod.seats.forEach(s -> {
                Integer old = result.computeIfAbsent(s, (a) -> { return 0;});
                result.put(s, old + coins);
            });
        }
        return result;
    }
}
