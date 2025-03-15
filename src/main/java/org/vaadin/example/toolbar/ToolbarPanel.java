package org.vaadin.example.toolbar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.vaadin.example.Miniature;
import org.vaadin.example.service.MiniaturesService;
import org.vaadin.example.service.OPRService;

import java.util.Map;

public class ToolbarPanel extends VerticalLayout {
    private final MiniaturesService miniaturesService;

    private final ListBox<Miniature> filesListBox = new ListBox<>();
    private final BaseDetails baseDetailsPanel;
    private final PreviewDetails previewPanel;
    private final OPRService oprService;
    private final PaddingDetails paddingImagePanel;

    public ToolbarPanel(MiniaturesService miniaturesService, PreviewDetails previewPanel, OPRService oprService) {
        this.miniaturesService = miniaturesService;
        this.previewPanel = previewPanel;
        this.oprService = oprService;
        this.paddingImagePanel = new PaddingDetails(miniaturesService::selected, previewPanel::update);
        this.baseDetailsPanel = new BaseDetails(miniaturesService, previewPanel::update);
        Details uploadImagePanel = new Details("Library", getUploadImagePanel());
        uploadImagePanel.addThemeVariants(DetailsVariant.SMALL, DetailsVariant.FILLED);
        uploadImagePanel.setWidthFull();
        uploadImagePanel.setOpened(true);
        setSpacing(false);
        add(uploadImagePanel, baseDetailsPanel, paddingImagePanel);
    }

    private Component getUploadImagePanel() {
        filesListBox.setRenderer(new ComponentRenderer<Component, Miniature>(miniature -> new Span(miniature.getFileName())));
        filesListBox.setItems(miniaturesService.miniatures());
        filesListBox.setWidthFull();
        Upload upload = getUpload();
        upload.setAcceptedFileTypes(".png", ".jpg");

        filesListBox.addValueChangeListener(e -> {
            miniaturesService.select(e.getValue()).ifPresentOrElse(miniature -> {
                baseDetailsPanel.init(miniature);
                paddingImagePanel.init(miniature);
                previewPanel.init(miniature);
            }, () -> {
                baseDetailsPanel.disable();
                paddingImagePanel.disable();
                previewPanel.disable();
            });
        });

        Button openImageBrowserButton = new Button("Conversions", e -> {
            Dialog dialog = new Dialog();
            VerticalLayout dialogLayout = new VerticalLayout();
            dialog.add(dialogLayout);

            Select<Map.Entry<String, String>> fractionSelect = new Select<>();
            fractionSelect.setItems(oprService.getArmies().entrySet());
            fractionSelect.setLabel("Fraction");
            fractionSelect.setRenderer(
                new ComponentRenderer<Component, Map.Entry<String, String>>(
                    miniature -> new Span(miniature.getKey())));

            Div stub = new Div();
            fractionSelect.addValueChangeListener(e1 ->
                dialogLayout.replace(stub, new Html("<div>" + oprService.getUnits(e1.getValue().getValue()) + "</div>")));

            dialogLayout.add(fractionSelect, stub);
            dialog.open();
        });

        return new VerticalLayout(filesListBox, new HorizontalLayout(upload, openImageBrowserButton));
    }

    private Upload getUpload() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropAllowed(false);

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            filesListBox.setItems(miniaturesService.addAndSelect(new Miniature(fileName, buffer.getInputStream(fileName))));
            upload.clearFileList();
        });

        upload.addFileRemovedListener(event -> {
            String fileName = event.getFileName();
            filesListBox.setItems(miniaturesService.remove(fileName));
        });
        return upload;
    }
}
