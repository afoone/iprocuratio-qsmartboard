/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iprocuratio.qsmartboard;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkex.zul.Columnchildren;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import ru.apertum.qsystem.common.CustomerState;
import ru.apertum.qsystem.common.QLog;
import ru.apertum.qsystem.smartboard.PrintRecords;

/**
 * @author Evgeniy Egorov
 * @author Alfonso Tienda
 */
public class QBoard {

    /**
     * Es el elemento de una fila, con las columnas correspondientes, para
     * pasarlo al zk
     * 
     * @author afoone
     *
     */
    public static class Str {

        private Label labelA;

        private Label labelB;
        final public Vbox strA;
        final public Vbox strB;

        public Str(Vbox aStrA, Vbox aStrB) {
            strA = aStrA;
            strB = aStrB;
        }

        public void clear() {
            if (labelA != null) {
                strA.removeChild(labelA);
            }
            if (labelB != null) {
                strB.removeChild(labelB);
            }
        }

        public Label getLabelA() {
            return labelA;
        }

        public Label getLabelB() {
            return labelB;
        }

        public Vbox getStrA() {
            return strA;
        }

        public Vbox getStrB() {
            return strB;
        }

        public boolean isReal() {
            return strA != null && strB != null;
        }

        public void set() {
            strA.appendChild(labelA);
            strB.appendChild(labelB);
        }

        public void set(Label labA, Label labB) {
            setLabs(labA, labB);
            set();
        }

        public void setLabelA(Label labelA) {
            this.labelA = labelA;
        }

        public void setLabelB(Label labelB) {
            this.labelB = labelB;
        }

        public void setLabs(Label labA, Label labB) {
            labelA = labA;
            labelB = labB;
        }

        /**
         * To string de la clase
         */
        @Override
        public String toString() {
            return "Vbox strA" + strA.toString() + "; Vbox strB: " + strB + "; labelA : " + labelA + "; labelB: " + labelB;
        }
    }

    private static String ALIGN_CENTER = "center";
    @Wire
    Columnchildren left;

    private final Map<Integer, Str> lines = new LinkedHashMap<Integer, Str>();

    private Boolean plFlag = null;

    @Wire
    Columnchildren right;

    /**
     * It is necessary to make include in the view and then bind @Wire(
     * "#incClientDashboard #incChangePriorityDialog #changePriorityDlg")
     *
     * @param view
     */
    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {

        /*
         * <!--div class="lineDivOdd" width="100%" height="14%" > <vbox
         * id="str1a" width="100%" height="100%" pack="center" align="center">
         * </vbox> </div>
         */
        Selectors.wireComponents(view, this, false);
        final int he = (100 - 16) / getLinesCount();
        final int lhe = 100 - 16 - he * (getLinesCount() - 1);
        for (int i = 1; i <= getLinesCount(); i++) {

            final Div da = new Div();
            da.setClass("lineDiv" + (i % 2 == 1 ? "Odd" : ""));
            da.setWidth("100%");
            da.setHeight((i == getLinesCount() ? lhe : he) + "%");
            final Vbox va = new Vbox();
            va.setId("str" + i + "a");
            va.setWidth("100%");
            va.setHeight("100%");
            va.setPack(ALIGN_CENTER);
            va.setAlign(ALIGN_CENTER);
            da.appendChild(va);
            left.appendChild(da);

            final Div db = new Div();
            db.setClass("lineDiv" + (i % 2 == 1 ? "Odd" : ""));
            db.setWidth("100%");
            db.setHeight((i == getLinesCount() ? lhe : he) + "%");
            final Vbox vb = new Vbox();
            vb.setId("str" + i + "b");
            vb.setWidth("100%");
            vb.setHeight("100%");
            vb.setPack(ALIGN_CENTER);
            vb.setAlign(ALIGN_CENTER);
            db.appendChild(vb);
            right.appendChild(db);

            lines.put(i, new Str(va, vb));
        }

        Selectors.wireComponents(view, this, false);
    }

    /**
     * Comprueba si está instalado el plugin de QSmartBoard
     * 
     * @return
     */
    private boolean checkPlugin() {
        if (plFlag == null) {
            try {
                Class.forName("ru.apertum.qsystem.smartboard.PrintRecords");

            } catch (ClassNotFoundException e) {
                QLog.l().logger().error("Plugin QSmartboardPlugin not found", e);
                plFlag = false;
            }
            // ¿Por qué está fuera del catch????
            plFlag = true;

        }
        return plFlag;
    }

    /**
     * Recupera los datos y los envía a la capa de vista zul
     */
    @Command("clickMe")
    public void clickMe() {
        // Comprueba que el plugin existe
        if (!checkPlugin()) {
            QLog.l().logger().error("El plugin de QSMARTBOARD no está presente");
            return;
        }

        if (PrintRecords.getInstance().isInvited()) {
            PrintRecords.getInstance().setInvited(false);
            QLog.l().logger().debug("QSMARTBOARD Nuevo Invitado :" + PrintRecords.getInstance().getRecords().get(0).customerNumber);
        }

        // Repasa todas las líneas

        for (int i = 1; i < lines.size(); i++) {
            final Str line = lines.get(i);
            line.clear();
            if (i <= PrintRecords.getInstance().getRecords().size()) {
                line.setLabelA(new Label(PrintRecords.getInstance().getRecords().get(i - 1).customerPrefix + PrintRecords.getInstance().getRecords().get(i - 1).customerNumber));
                line.setLabelB(new Label(PrintRecords.getInstance().getRecords().get(i - 1).point));

                final boolean blink = PrintRecords.getInstance().getRecords().get(i - 1).getState() == CustomerState.STATE_INVITED;
                line.getLabelA().setClass(blink ? "blink_me" : "no_blink");
                line.getLabelB().setClass(blink ? "blink_me" : "no_blink");

                line.set();
            }
        }

    }

    // ************************************************************************************************************************************************
    // ************************************************************************************************************************************************
    // Panel settings
    // ************************************************************************************************************************************************
    // ************************************************************************************************************************************************

    public String getBottomSize() {
        return checkPlugin() ? PrintRecords.getInstance().getBottomSize() : "0px";
    }

    public String getBottomUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getBottomUrl() : "";
    }

    public boolean getBottomVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getBottomSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getColumnFirst() {
        return checkPlugin() ? PrintRecords.getInstance().getColumnFirst() : "For clients";
    }

    public String getColumnSecond() {
        return checkPlugin() ? PrintRecords.getInstance().getColumnSecond() : "To point";
    }

    public String getLeftSize() {
        return checkPlugin() ? PrintRecords.getInstance().getLeftSize() : "0px";
    }

    public String getLeftUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getLeftUrl() : "";
    }

    public boolean getLeftVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getLeftSize().replaceAll("0|%|(px)", "")) : false;
    }

    public int getLinesCount() {
        return checkPlugin() ? PrintRecords.getInstance().getLinesCount() : 6;
    }

    public String getRightSize() {
        return checkPlugin() ? PrintRecords.getInstance().getRightSize() : "0px";
    }

    public String getRightUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getRightUrl() : "";
    }

    public boolean getRightVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getRightSize().replaceAll("0|%|(px)", "")) : false;
    }

    public String getTopSize() {
        return checkPlugin() ? PrintRecords.getInstance().getTopSize() : "0px";
    }

    public String getTopUrl() {
        return checkPlugin() ? PrintRecords.getInstance().getTopUrl() : "";
    }

    public boolean getTopVisible() {
        return checkPlugin() ? !"".equals(PrintRecords.getInstance().getTopSize().replaceAll("0|%|(px)", "")) : false;
    }

}
