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
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

public class ExtractContent implements Callable {

    protected final Logger logger = LogManager.getLogger(getClass());

    public ExtractContent() {
    }

    @Override
    public Object onCall(MuleEventContext eventContext) throws Exception {

        MuleMessage message = eventContext.getMessage();

        DataHandler dh = (DataHandler) message.getPayload();
        logger.debug("ContentType:" + dh.getContentType().replace("\r\n", "").replace("\t", ""));

        try {
            // Multipart Body
            if (dh.getContent() instanceof MimeMultipart) {
                logger.info("Multipart Body found");
                String mimeType = "text/plain";
                MimeMultipart mime = (MimeMultipart) dh.getContent();
                for (int i = 0; i < mime.getCount(); i++) {
                    BodyPart part = mime.getBodyPart(i);
                    String contentType = part.getContentType().replace("\r\n", "").replace("\t", "");
                    logger.debug("PartType:" + contentType + "\n" + part.getContent().toString());
                    // Get plain/text body
                    if (mimeType.equals(contentType.split(";")[0])) {
                        message.setProperty("content", "body", PropertyScope.INVOCATION);
                        message.setProperty("contentType", mimeType, PropertyScope.INVOCATION);
                        message.setPayload(part.getContent().toString());
                        message.getDataType().setMimeType(mimeType);
                    }
                }
            }
            // Attachments
            else {
                String fileName = dh.getName();
                InputStream istream = dh.getInputStream();
                String result = new BufferedReader(new InputStreamReader(istream)).lines().collect(Collectors.joining("\n"));
                String mimeType = new MimetypesFileTypeMap().getContentType(fileName);
                logger.info("Attachment found " + fileName + " " + mimeType);
                logger.debug("Content:\n" + result);
                message.setProperty("content", "attachment", PropertyScope.INVOCATION);
                message.setProperty("file", fileName, PropertyScope.INVOCATION);
                message.setProperty("contentType", mimeType, PropertyScope.INVOCATION);
                message.setPayload(result);
                message.getDataType().setMimeType(mimeType);
            }

        } catch (IOException | MessagingException e) {
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return message;
    }

}
