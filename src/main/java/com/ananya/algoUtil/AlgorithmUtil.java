package com.ananya.algoUtil;

import com.ananya.vo.Transaction;

import java.util.*;

public class AlgorithmUtil {
    public static Map<String, Double> memberNetBalance;
    private static PriorityQueue<Member> getters;
    private static PriorityQueue<Member> givers;
    public static List<String> minimizedTransactions;
    public static Integer minTsnCounter;

    public static void calculateNetBalances(List<Transaction> transactions){
        // getting payer, amt, payees, description
        // outgoing to be added // incoming to be subtracted // netbalance = outgoing - incoming

        for(Transaction tsn : transactions){
            //adding outgoing for the payer
            System.out.println("net balance update for payer " + tsn.getPayer() + " amt: " +  memberNetBalance.getOrDefault(tsn.getPayer(), 0.0) + tsn.getAmount());
            memberNetBalance.put(tsn.getPayer(), roundToTwoDecimals(memberNetBalance.getOrDefault(tsn.getPayer(), 0.0) + tsn.getAmount()));

            //adding incoming for all the payees
            Double splitAmt = tsn.getAmount()/tsn.getPayee().size();
            for(String payee : tsn.getPayee()){
                System.out.println("net balance update for payee " + payee + " amt: " + (memberNetBalance.getOrDefault(payee, 0.0) - splitAmt));
                memberNetBalance.put(payee, roundToTwoDecimals(memberNetBalance.getOrDefault(payee, 0.0) - splitAmt));
            }
        }

        System.out.println("Calculated Net Balances for the members :: " + memberNetBalance.toString());
    }

    public static void createGettersGiversList(){
        getters = new PriorityQueue<>(Comparator.comparing(getter -> -Math.abs(getter.netBalance)));
        givers = new PriorityQueue<>(Comparator.comparing(giver -> -Math.abs(giver.netBalance)));
        getters.clear();
        givers.clear();

        for(String member: memberNetBalance.keySet()){
            //if netbalance > 0 -> outgoing is more -> member is a getter
            //if netbalance < 0 -> incoming more -> member is a giver
            Double balance = memberNetBalance.get(member);
            if(balance > 0.0){
                getters.add(new Member(member, balance));
            }
            else if(balance < 0){
                givers.add(new Member(member, balance));
            }
        }

        System.out.println("Getters are :: " + getters.toString());
        System.out.println("Givers are :: " + givers.toString());
    }

    public static List<String> getMinimizedTransactions(){
        minimizedTransactions = new ArrayList<>();
        minTsnCounter = 0;

        try {
            while(!givers.isEmpty() && !getters.isEmpty()){
                Member getter = getters.remove();
                Member giver = givers.remove();
                System.out.println(" queue removal: getter=" + getter.netBalance + " giver="+giver.netBalance);

                double net = roundToTwoDecimals(getter.netBalance + giver.netBalance);
                // if net is +ve -> getter debt is not clear, balance getters need to be added again and vice versa
                // if net is 0 -> payment settled between the two
                if(net > 0.0){
                    minimizedTransactions.add(giver.member + " owes Rs. " + Math.abs(giver.netBalance) + " to " + getter.member);
                    System.out.println("getter's pay not resolved by amount: " + net + ", hence adding getter to queue again");
                    getters.add(new Member(getter.member, net));
                }
                else if(net < 0.0){
                    minimizedTransactions.add(giver.member + " owes Rs. " + Math.abs(getter.netBalance) + " to " + getter.member);
                    System.out.println("giver didn't repay in full (amount remaining): " + net + ", hence adding giver to queue again.");
                    givers.add(new Member(giver.member, net));
                } else {
                    minimizedTransactions.add(giver.member + " owes Rs. " + getter.netBalance + " to " + getter.member);
                    System.out.println("Paid in full! Rs. " + getter.netBalance);
                }
                minTsnCounter++;
            }
            return minimizedTransactions;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonList("Some problem occured!");
        }
    }

    static class Member{
        String member;
        Double netBalance;

        public Member(String member, Double netBalance) {
            this.member = member;
            this.netBalance = netBalance;
        }

        @Override
        public String toString() {
            return "Member{" +
                    "member='" + member + '\'' +
                    ", netBalance=" + netBalance +
                    '}';
        }
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
