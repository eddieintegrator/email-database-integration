package cz.aimtec.gmail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;

public class TransformBodyPart extends AbstractMessageTransformer {

    protected final Logger logger = LogManager.getLogger(getClass());

    @Override
    public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

        DataHandler dh = (DataHandler) message.getPayload();
        logger.debug("ContentType:" + dh.getContentType().replace("\r\n", "").replace("\t", ""));

        try {
            // Multipart Body
            if (dh.getContent() instanceof MimeMultipart) {
                MimeMultipart mime = (MimeMultipart) dh.getContent();
                for (int i = 0; i < mime.getCount(); i++) {
                    BodyPart part = mime.getBodyPart(i);
                    String contentType = part.getContentType().replace("\r\n", "").replace("\t", "");
                    logger.debug("PartType:" + contentType + "\n" + part.getContent().toString());
                    // Get plain/text body
                    if ("text/plain".equals(contentType.split(";")[0])) {
                        message.setProperty("content", "body", PropertyScope.INVOCATION);
                        message.setProperty("contentType", "text/plain", PropertyScope.INVOCATION);
                        message.setPayload(part.getContent().toString());
                    }
                }
            }
            // Attachments
            else {
                logger.debug("Name: " + dh.getName());
                InputStream istream = dh.getInputStream();
                String result = new BufferedReader(new InputStreamReader(istream)).lines().collect(Collectors.joining("\n"));
                String mimeType = new MimetypesFileTypeMap().getContentType(dh.getName());
                logger.debug("Content:\n" + result);
                message.setProperty("content", "attachment", PropertyScope.INVOCATION);
                message.setProperty("file", dh.getName(), PropertyScope.INVOCATION);
                message.setProperty("contentType", mimeType, PropertyScope.INVOCATION);
                message.setPayload(result);
            }

        } catch (IOException | MessagingException e) {
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return message;
    }

}
