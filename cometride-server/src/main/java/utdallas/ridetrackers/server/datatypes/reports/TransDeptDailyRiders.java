package utdallas.ridetrackers.server.datatypes.reports;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by matt lautz on 4/26/2015.
 */
public class TransDeptDailyRiders {

    private String date;
    private int seven;
    private int eight;
    private int nine;
    private int sevenToNineTotal;
    private int ten;
    private int eleven;
    private int twelve;
    private int tenToTwelveTotal;
    private int thirteen;
    private int fourteen;
    private int fifteen;
    private int thirteenToFifteenTotal;
    private int sixteen;
    private int seventeen;
    private int eighteen;
    private int sixteenToEighteenTotal;
    private int nineteen;
    private int twenty;
    private int twentyOne;
    private int nineteenToTwentyOneTotal;
    private int total;

    public TransDeptDailyRiders() {
        this("",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
    }

    public TransDeptDailyRiders(String date, int seven, int eight,
                                int nine, int sevenToNineTotal, int ten,
                                int eleven, int twelve, int tenToTwelveTotal,
                                int thirteen, int fourteen, int fifteen,
                                int thirteenToFifteenTotal, int sixteen,
                                int seventeen, int eighteen, int sixteenToEighteenTotal,
                                int nineteen, int twenty, int twentyOne,
                                int nineteenToTwentyOneTotal, int total) {

        this.date = date;
        this.seven = seven;
        this.eight = eight;
        this.nine = nine;
        this.sevenToNineTotal = sevenToNineTotal;
        this.ten = ten;
        this.eleven = eleven;
        this.twelve = twelve;
        this.tenToTwelveTotal = tenToTwelveTotal;
        this.thirteen = thirteen;
        this.fourteen = fourteen;
        this.fifteen = fifteen;
        this.thirteenToFifteenTotal = thirteenToFifteenTotal;
        this.sixteen = sixteen;
        this.seventeen = seventeen;
        this.eighteen = eighteen;
        this.sixteenToEighteenTotal = sixteenToEighteenTotal;
        this.nineteen = nineteen;
        this.twenty = twenty;
        this.twentyOne = twentyOne;
        this.nineteenToTwentyOneTotal = nineteenToTwentyOneTotal;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSeven() {
        return seven;
    }

    public void setSeven(int seven) {
        this.seven = seven;
    }

    public int getEight() {
        return eight;
    }

    public void setEight(int eight) {
        this.eight = eight;
    }

    public int getNine() {
        return nine;
    }

    public void setNine(int nine) {
        this.nine = nine;
    }

    public int getSevenToNineTotal() {
        return getSeven() + getEight() + getNine();
    }

    public int getTen() {
        return ten;
    }

    public void setTen(int ten) {
        this.ten = ten;
    }

    public int getEleven() {
        return eleven;
    }

    public void setEleven(int eleven) {
        this.eleven = eleven;
    }

    public int getTwelve() {
        return twelve;
    }

    public void setTwelve(int twelve) {
        this.twelve = twelve;
    }

    public int getTenToTwelveTotal() {
        return getTen() + getEleven() + getTwelve();
    }

    public int getThirteen() {
        return thirteen;
    }

    public void setThirteen(int thirteen) {
        this.thirteen = thirteen;
    }

    public int getFourteen() {
        return fourteen;
    }

    public void setFourteen(int fourteen) {
        this.fourteen = fourteen;
    }

    public int getFifteen() {
        return fifteen;
    }

    public void setFifteen(int fifteen) {
        this.fifteen = fifteen;
    }

    public int getThirteenToFifteenTotal() {
        return getThirteen() + getFourteen() + getFifteen();
    }

    public int getSixteen() {
        return sixteen;
    }

    public void setSixteen(int sixteen) {
        this.sixteen = sixteen;
    }

    public int getSeventeen() {
        return seventeen;
    }

    public void setSeventeen(int seventeen) {
        this.seventeen = seventeen;
    }

    public int getEighteen() {
        return eighteen;
    }

    public void setEighteen(int eighteen) {
        this.eighteen = eighteen;
    }

    public int getSixteenToEighteenTotal() {
        return getSixteen() + getSeventeen() + getEighteen();
    }

    public int getNineteen() {
        return nineteen;
    }

    public void setNineteen(int nineteen) {
        this.nineteen = nineteen;
    }

    public int getTwenty() {
        return twenty;
    }

    public void setTwenty(int twenty) {
        this.twenty = twenty;
    }

    public int getTwentyOne() {
        return twentyOne;
    }

    public void setTwentyOne(int twentyOne) {
        this.twentyOne = twentyOne;
    }

    public int getNineteenToTwentyOneTotal() {
        return getNineteen() + getTwenty() + getTwentyOne();
    }

    public int getTotal() {
        return getSevenToNineTotal() + getTenToTwelveTotal() +
                getThirteenToFifteenTotal() + getSixteenToEighteenTotal() +
                getNineteenToTwentyOneTotal();
    }
}
