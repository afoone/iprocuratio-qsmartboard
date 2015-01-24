/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.qboard;

import java.util.LinkedHashMap;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.server.controller.AIndicatorBoard.Record;
import ru.apertum.qsystem.smartboard.PrintRecords;

/**
 *
 * @author Evgeniy Egorov
 */
public class QBoard {

    /**
     * Это нужно чтоб делать include во view и потом связывать @Wire("#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
     *
     * @param view
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        lines.put(1, new Str(str1a, str1b));
        lines.put(2, new Str(str2a, str2b));
        lines.put(3, new Str(str3a, str3b));
        lines.put(4, new Str(str4a, str4b));
        lines.put(5, new Str(str5a, str5b));
        lines.put(6, new Str(str6a, str6b));
        lines.put(7, new Str(str7a, str7b));
        lines.put(8, new Str(str8a, str8b));
        lines.put(9, new Str(str9a, str9b));
        lines.put(10, new Str(str10a, str10b));
        lines.put(11, new Str(str11a, str11b));
        lines.put(12, new Str(str12a, str12b));
    }
    private final LinkedHashMap<Integer, Str> lines = new LinkedHashMap<>();

    private Boolean plFlag = null;

    private boolean checkPlugin() {
        if (plFlag == null) {
            try {
                Class.forName("ru.apertum.qsystem.smartboard.PrintRecords");
            } catch (ClassNotFoundException e) {
                System.out.println("Plugin QSmartboardPlugin not found.");
                plFlag = false;
            }
            plFlag = true;
        }
        return plFlag;
    }

    @Command("clickMe")
    public void clickMe() {
        if (!checkPlugin()) {
            return;
        }
        if (PrintRecords.getInstance().isInvited()) {
            PrintRecords.getInstance().setInvited(false);
            Clients.evalJavaScript("DHTMLSound()");
        }

        final Record[] recs = PrintRecords.getInstance().getRecords().toArray(new Record[PrintRecords.getInstance().getRecords().size()]);
        //System.out.println("recs.length="+recs.length + " / " + PrintRecords.getInstance().getRecords().toString());
        for (int i = 1; i < lines.size(); i++) {
            final Str line = lines.get(i);
            if (line.isReal()) {
                line.clear();
                if (i <= recs.length) {
                    line.labelA = new Label(recs[i - 1].customerNumber);
                    line.labelB = new Label(recs[i - 1].point);

                    final boolean blink = (recs[i - 1].getState() == CustomerState.STATE_INVITED || recs[i - 1].getState() == CustomerState.STATE_INVITED);
                    line.labelA.setClass(blink ? "blink_me" : "no_blink");
                    line.labelB.setClass(blink ? "blink_me" : "no_blink");

                    line.set();
                }
            }
        }

    }

    public static class Str {

        final public Vbox strA;
        final public Vbox strB;
        public Label labelA;
        public Label labelB;

        public Str(Vbox strA, Vbox strB) {
            this.strA = strA;
            this.strB = strB;
        }

        public void setLabs(Label labA, Label labB) {
            labelA = labA;
            labelB = labB;
        }

        public void set() {
            strA.appendChild(labelA);
            strB.appendChild(labelB);
        }

        public void set(Label labA, Label labB) {
            setLabs(labA, labB);
            set();
        }

        public boolean isReal() {
            return strA != null && strB != null;
        }

        public void clear() {
            if (labelA != null) {
                strA.removeChild(labelA);
            }
            if (labelB != null) {
                strB.removeChild(labelB);
            }
        }
    }

    @Wire
    Vbox str1a;
    @Wire
    Vbox str1b;
    @Wire
    Vbox str2a;
    @Wire
    Vbox str2b;
    @Wire
    Vbox str3a;
    @Wire
    Vbox str3b;
    @Wire
    Vbox str4a;
    @Wire
    Vbox str4b;
    @Wire
    Vbox str5a;
    @Wire
    Vbox str5b;
    @Wire
    Vbox str6a;
    @Wire
    Vbox str6b;
    @Wire
    Vbox str7a;
    @Wire
    Vbox str7b;
    @Wire
    Vbox str8a;
    @Wire
    Vbox str8b;
    @Wire
    Vbox str9a;
    @Wire
    Vbox str9b;
    @Wire
    Vbox str10a;
    @Wire
    Vbox str10b;
    @Wire
    Vbox str11a;
    @Wire
    Vbox str11b;
    @Wire
    Vbox str12a;
    @Wire
    Vbox str12b;

    // ************************************************************************************************************************************************
    // ************************************************************************************************************************************************
    // Настройки табло
    // ************************************************************************************************************************************************
    // ************************************************************************************************************************************************
    public String getTopSize() {
        return checkPlugin() ? PrintRecords.getInstance().getTopSize() : "0px";
    }

    public boolean getTopVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getTopSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getTopUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getTopUrl() : "";
    }

    public String getLeftSize() {
        return checkPlugin() ? PrintRecords.getInstance().getLeftSize() : "0px";
    }

    public boolean getLeftVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getLeftSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getLeftUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getLeftUrl() : "";
    }

    public String getRightSize() {
        return checkPlugin() ? PrintRecords.getInstance().getRightSize() : "0px";
    }

    public boolean getRightVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getRightSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getRightUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getRightUrl() : "";
    }

    public String getBottomSize() {
        return checkPlugin() ? PrintRecords.getInstance().getBottomSize() : "0px";
    }

    public boolean getBottomVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getBottomSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getBottomUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getBottomUrl() : "";
    }

    public String getColumnFirst() {
        return checkPlugin() ? PrintRecords.getInstance().getColumnFirst() : "For clients";
    }

    public String getColumnSecond() {
        return checkPlugin() ? PrintRecords.getInstance().getColumnSecond() : "To point";
    }

}
