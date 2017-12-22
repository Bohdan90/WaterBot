package my.bot;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.events.AccountLinkingEvent;
import com.github.messenger4j.receive.handlers.*;
import com.github.messenger4j.send.*;
import my.bot.dao.GetResultsDao;
import my.bot.dao.SetDatesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by aboullaite on 2017-02-26.
 */

@RestController
@RequestMapping("/callback")
public class CallBackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallBackHandler.class);

    private static final String RESOURCE_URL =
            "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";
    public static final String GOOD_ACTION = "DEVELOPER_DEFINED_PAYLOAD_FOR_GOOD_ACTION";
    public static final String NOT_GOOD_ACTION = "DEVELOPER_DEFINED_PAYLOAD_FOR_NOT_GOOD_ACTION";

    private final MessengerReceiveClient receiveClient;
    private final MessengerSendClient sendClient;

    /**
     * Constructs the {@code CallBackHandler} and initializes the {@code MessengerReceiveClient}.
     *
     * @param appSecret   the {@code Application Secret}
     * @param verifyToken the {@code Verification Token} that has been provided by you during the setup of the {@code
     *                    Webhook}
     * @param sendClient  the initialized {@code MessengerSendClient}
     */
    @Autowired
    public CallBackHandler(@Value("${messenger4j.appSecret}") final String appSecret,
                                            @Value("${messenger4j.verifyToken}") final String verifyToken,
                                            final MessengerSendClient sendClient) {

        logger.debug("Initializing MessengerReceiveClient - appSecret: {} | verifyToken: {}", appSecret, verifyToken);
        this.receiveClient = MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
                .onTextMessageEvent(newTextMessageEventHandler())
                .onQuickReplyMessageEvent(newQuickReplyMessageEventHandler())
                .onPostbackEvent(newPostbackEventHandler())
                .onAccountLinkingEvent(newAccountLinkingEventHandler())
                .onOptInEvent(newOptInEventHandler())
                .onEchoMessageEvent(newEchoMessageEventHandler())
                .onMessageDeliveredEvent(newMessageDeliveredEventHandler())
                .onMessageReadEvent(newMessageReadEventHandler())
                .fallbackEventHandler(newFallbackEventHandler())
                .build();

        this.sendClient = sendClient;
    }

    /**
     * Webhook verification endpoint.
     *
     * The passed verification token (as query parameter) must match the configured verification token.
     * In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam("hub.mode") final String mode,
                                                @RequestParam("hub.verify_token") final String verifyToken,
                                                @RequestParam("hub.challenge") final String challenge) {

        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode,
                verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode, verifyToken, challenge));
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
                                               @RequestHeader("X-Hub-Signature") final String signature) {

        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);

        try {
            this.receiveClient.processCallbackPayload(payload, signature);
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    private String senderTempId = null;
    private String clientName = null;
   private Float  drinkCount = null;
    private Float  drinkTempCount = null;
   private boolean rewriteDataFlag = false;
    private boolean  isOldUser = false;
    @Inject
    private JdbcTemplate hsqlTemplate;
    private TextMessageEventHandler newTextMessageEventHandler() {
        return event -> {
            logger.debug("Received TextMessageEvent: {}", event);
            GetResultsDao gs = new GetResultsDao();



            final String messageId = event.getMid();
            final String messageText = event.getText();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received message '{}' with text '{}' from user '{}' at '{}'",
                    messageId, messageText, senderId, timestamp);

            try {
                        List<Map<String, Object>> gettedDataList = gs.getAllDates(senderId,hsqlTemplate);

               if (gettedDataList!=null && gettedDataList.size()>0){
                   isOldUser=true;
               }

                if (!isOldUser) {
                    if (senderTempId == null) {
                        senderTempId = senderId;
                        sendTextMessage(senderId, "Hello, first time I've seen you. What`s your name mate?");
                    } else if ( senderTempId != null && clientName == null) {
                        sendQuickReply(senderId, "So i will call you " + messageText);
                        clientName = messageText;
                    } else if ( senderTempId != null && clientName != null && drinkCount == null && rewriteDataFlag == false) {
                        //sendReadReceipt(senderId);
                        //  sendSpringDoc(senderId, messageText);

                        checkWaterCount(messageText);
                        //  sendTypingOff(senderId);
                    }
                }else if (isOldUser){
                    if (!rewriteDataFlag) {
                        clientName = gettedDataList.get(0).get("NAME").toString();
                        drinkCount = Float.valueOf(gettedDataList.get(0).get("WATER").toString());
                        senderTempId = senderId;
                        sendQuickReply(senderId, "Hello," + clientName + " do you change the number of water? Last time it was -" + drinkCount + " per day");
                    }else {
                        clientName = gettedDataList.get(0).get("NAME").toString();
                        drinkCount = Float.valueOf(gettedDataList.get(0).get("WATER").toString());
                        senderTempId = senderId;
                        checkWaterCount(messageText);
                    }
                              //  sendTextMessage(senderId, "Hello," +clientName + " do you change the number of water? Last time it was -"+drinkCount + " per day");

                        }

            } catch (MessengerApiException | MessengerIOException e) {
                handleSendException(e);
            }
        };
    }


    private void clearValues(){
        senderTempId = null;
        clientName = null;
        drinkCount = null;
        isOldUser = false;

        rewriteDataFlag= false;
    }


    private void sendGifMessage(String recipientId, String gif) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendImageAttachment(recipientId, gif);
    }



    private void sendQuickReply(String recipientId, String replyText) throws MessengerApiException, MessengerIOException {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()

                .addTextQuickReply("Looks good", GOOD_ACTION).toList()
                .addTextQuickReply("Nope!", NOT_GOOD_ACTION).toList()
                .build();

        this.sendClient.sendTextMessage(recipientId, replyText, quickReplies);
    }



    private void sendTypingOn(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.TYPING_ON);
    }

    private void sendTypingOff(String recipientId) throws MessengerApiException, MessengerIOException {
        this.sendClient.sendSenderAction(recipientId, SenderAction.TYPING_OFF);
    }

    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);


            try {
                if (!isOldUser) {
                    if (quickReplyPayload.equals(GOOD_ACTION)) {

                        sendTextMessage(senderId, "Nice to meet you mate! ");
                        sendGifMessage(senderId, "https://media1.tenor.com/images/888de7ec66dd5053c46d4dba5b415003/tenor.gif?itemid=3455563");

                        this.sendClient.sendSenderAction(senderId, SenderAction.TYPING_ON);
                        sendTextMessage(senderId, "And how much you drink? ");

                    } else {
                        clientName = null;



                        sendTextMessage(senderId, "So give me your name!");
                    }
                }else if (isOldUser ) {
                    if (quickReplyPayload.equals(GOOD_ACTION)) {
                        this.sendClient.sendSenderAction(senderId, SenderAction.TYPING_ON);
                        sendTextMessage(senderId, "And what your current water consume? ");

                        rewriteDataFlag = true;


                    } else{



                        sendTextMessage(senderId, "So carry on, bro!");
                    }
                }
            }catch (MessengerApiException e) {
                e.printStackTrace();
            } catch (MessengerIOException e) {
                e.printStackTrace();
            }
        };
    }

    private void checkWaterCount (String messageText){
        SetDatesDao sd = new SetDatesDao();
            try {
                drinkCount = Float.valueOf(messageText);
                if (drinkCount > 2) {
                    sendTextMessage(senderTempId, "You do all right "+ clientName+ ". Arevua");
                    sd.setAllDates(senderTempId, clientName, drinkCount.toString(), hsqlTemplate);
                    clearValues();
                } else {
                    sendTextMessage(senderTempId, "Try to drink more");
                    sd.setAllDates(senderTempId, clientName, drinkCount.toString(), hsqlTemplate);
                    clearValues();
                }

            } catch (NumberFormatException nfe) {
        logger.error(nfe.getMessage());
        sendTextMessage(senderTempId, "No," + messageText + " just number of litres");
    }
    }
    private PostbackEventHandler newPostbackEventHandler() {
        return event -> {
            logger.debug("Received PostbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String payload = event.getPayload();
            final Date timestamp = event.getTimestamp();

            logger.info("Received postback for user '{}' and page '{}' with payload '{}' at '{}'",
                    senderId, recipientId, payload, timestamp);

            sendTextMessage(senderId, "Postback called");
        };
    }

    private AccountLinkingEventHandler newAccountLinkingEventHandler() {
        return event -> {
            logger.debug("Received AccountLinkingEvent: {}", event);

            final String senderId = event.getSender().getId();
            final AccountLinkingEvent.AccountLinkingStatus accountLinkingStatus = event.getStatus();
            final String authorizationCode = event.getAuthorizationCode();

            logger.info("Received account linking event for user '{}' with status '{}' and auth code '{}'",
                    senderId, accountLinkingStatus, authorizationCode);
        };
    }

    private OptInEventHandler newOptInEventHandler() {
        return event -> {
            logger.debug("Received OptInEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String passThroughParam = event.getRef();
            final Date timestamp = event.getTimestamp();

            logger.info("Received authentication for user '{}' and page '{}' with pass through param '{}' at '{}'",
                    senderId, recipientId, passThroughParam, timestamp);

            sendTextMessage(senderId, "Authentication successful");
        };
    }

    private EchoMessageEventHandler newEchoMessageEventHandler() {
        return event -> {
            logger.debug("Received EchoMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String recipientId = event.getRecipient().getId();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received echo for message '{}' that has been sent to recipient '{}' by sender '{}' at '{}'",
                    messageId, recipientId, senderId, timestamp);
        };
    }

    private MessageDeliveredEventHandler newMessageDeliveredEventHandler() {
        return event -> {
            logger.debug("Received MessageDeliveredEvent: {}", event);

            final List<String> messageIds = event.getMids();
            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            if (messageIds != null) {
                messageIds.forEach(messageId -> {
                    logger.info("Received delivery confirmation for message '{}'", messageId);
                });
            }

            logger.info("All messages before '{}' were delivered to user '{}'", watermark, senderId);
        };
    }

    private MessageReadEventHandler newMessageReadEventHandler() {
        return event -> {
            logger.debug("Received MessageReadEvent: {}", event);

            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            logger.info("All messages before '{}' were read by user '{}'", watermark, senderId);
        };
    }

    /**
     * This handler is called when either the message is unsupported or when the event handler for the actual event type
     * is not registered. In this showcase all event handlers are registered. Hence only in case of an
     * unsupported message the fallback event handler is called.
     */
    private FallbackEventHandler newFallbackEventHandler() {
        return event -> {
            logger.debug("Received FallbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            logger.info("Received unsupported message from user '{}'", senderId);
        };
    }

    private void sendTextMessage(String recipientId, String text) {
        try {
            final Recipient recipient = Recipient.newBuilder().recipientId(recipientId).build();
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            this.sendClient.sendTextMessage(recipient, notificationType, text, metadata);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

    private void handleIOException(Exception e) {
        logger.error("Could not open Spring.io page. An unexpected error occurred.", e);
    }
}
