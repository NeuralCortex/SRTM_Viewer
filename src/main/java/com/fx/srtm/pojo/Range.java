package com.fx.srtm.pojo;

/**
 *
 * @author pscha
 */
public class Range {

    private int xStart;
    private int xEnde;
    private int yStart;
    private int yEnde;
    private int horStep;
    private int verStep;

    public Range(int xStart, int xEnde, int yStart, int yEnde, int horStep, int verStep) {
        this.xStart = xStart;
        this.xEnde = xEnde;
        this.yStart = yStart;
        this.yEnde = yEnde;
        this.horStep = horStep;
        this.verStep = verStep;
    }

    public int getxStart() {
        return xStart;
    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getxEnde() {
        return xEnde;
    }

    public void setxEnde(int xEnde) {
        this.xEnde = xEnde;
    }

    public int getyStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getyEnde() {
        return yEnde;
    }

    public void setyEnde(int yEnde) {
        this.yEnde = yEnde;
    }

    public int getHorStep() {
        return horStep;
    }

    public void setHorStep(int horStep) {
        this.horStep = horStep;
    }

    public int getVerStep() {
        return verStep;
    }

    public void setVerStep(int verStep) {
        this.verStep = verStep;
    }
}
