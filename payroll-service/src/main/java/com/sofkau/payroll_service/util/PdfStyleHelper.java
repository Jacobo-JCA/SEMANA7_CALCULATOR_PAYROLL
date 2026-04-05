package com.sofkau.payroll_service.util;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import java.awt.*;

public class PdfStyleHelper {
    public static final Font TITLE_FONT = FontFactory.getFont(
            FontFactory.HELVETICA_BOLD, 22, Color.BLACK);

    public static final Font SUBTITLE_FONT = FontFactory.getFont(
            FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);

    public static final Font NORMAL_FONT = FontFactory.getFont(
            FontFactory.HELVETICA, 10, Color.BLACK);

    public static final Font BOLD_FONT = FontFactory.getFont(
            FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

    public static final Font NET_FONT = FontFactory.getFont(
            FontFactory.HELVETICA_BOLD, 18, new Color(0, 77, 179));

    public static final Color HEADER_BG_COLOR = Color.LIGHT_GRAY;
    public static final Color NET_BG_COLOR = new Color(240, 245, 255);
    public static final Color NET_BORDER_COLOR = new Color(0, 77, 179);

    private PdfStyleHelper() {
        throw new IllegalStateException("Utility class");
    }
}
