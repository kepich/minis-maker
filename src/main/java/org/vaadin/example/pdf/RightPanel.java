package org.vaadin.example.pdf;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import org.vaadin.example.service.MiniaturesService;

import java.io.IOException;

public class RightPanel extends HorizontalLayout {

    public RightPanel(MiniaturesService miniaturesService) throws IOException {
        PdfViewer pdfViewer = new PdfViewer(miniaturesService);
        Button button = new Button("Generate PDF", e -> {
            try {
                pdfViewer.update();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Scroller scroller = new Scroller(pdfViewer);
        scroller.setWidthFull();
        add(scroller, button);
    }
}
