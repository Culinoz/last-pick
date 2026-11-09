package com.mtg.mtgDraft.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import com.mtg.mtgDraft.model.Card;

@Service
public class PdfService {

	public byte[] generateCardsPdf(List<Card> cards) throws IOException {
		try (PDDocument document = new PDDocument()) {
			if (cards != null && !cards.isEmpty()) {
				// Standard MTG card aspect ratio roughly 63mm x 88mm (approx 140x195 points)
				float cardWidth = 140;
				float cardHeight = 195;
				float marginX = 10;
				float marginY = 10;
				float spacingX = 1;
				float spacingY = 1;

				int cols = 4;
				int rows = 4;
				int cardsPerPage = cols * rows;

				PDPage page = null;
				PDPageContentStream contentStream = null;

				for (int i = 0; i < cards.size(); i++) {
					if (i % cardsPerPage == 0) {
						if (contentStream != null) {
							contentStream.close();
						}
						page = new PDPage(PDRectangle.A4);
						document.addPage(page);
						contentStream = new PDPageContentStream(document, page);
					}

					Card card = cards.get(i);
					int pageIndex = i % cardsPerPage;
					int col = pageIndex % cols;
					int row = pageIndex / cols;

					// PDFBox (0,0) is bottom-left
					float x = marginX + col * (cardWidth + spacingX);
					float y = PDRectangle.A4.getHeight() - marginY - (row + 1) * cardHeight - row * spacingY;

					if (card.getImageUri() != null && !card.getImageUri().isEmpty()) {
						try {
							URI uri = URI.create(card.getImageUri());
							byte[] imageBytes;
							try (InputStream in = uri.toURL().openStream()) {
								imageBytes = in.readAllBytes();
							}
							PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes,
									card.getName());
							contentStream.drawImage(pdImage, x, y, cardWidth, cardHeight);
						} catch (Exception e) {
							System.err.println(
									"Could not load image for card: " + card.getName() + " - " + e.getMessage());
						}
					}
				}

				if (contentStream != null) {
					contentStream.close();
				}
			} else {
				PDPage page = new PDPage(PDRectangle.A4);
				document.addPage(page);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			document.save(baos);
			return baos.toByteArray();
		}
	}
}