/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.psnc.dl.wf4ever.portal.components.feedback;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import pl.psnc.dl.wf4ever.portal.events.FeedbackEvent;

/**
 * A panel that displays {@link org.apache.wicket.feedback.FeedbackMessage}s in a list view. The maximum number of
 * messages to show can be set with setMaxMessages().
 * 
 * @see org.apache.wicket.feedback.FeedbackMessage
 * @see org.apache.wicket.feedback.FeedbackMessages
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public class MyFeedbackPanel extends Panel implements IFeedback {

    /**
     * List for messages.
     */
    private final class MessageListView extends ListView<FeedbackMessage> {

        /** id. */
        private static final long serialVersionUID = 1L;


        /**
         * Constructor.
         * 
         * @see org.apache.wicket.Component#Component(String)
         * @param id
         *            id
         */
        public MessageListView(final String id) {
            super(id);
            setDefaultModel(newFeedbackMessagesModel());
        }


        @Override
        protected void populateItem(final ListItem<FeedbackMessage> listItem) {
            final FeedbackMessage message = listItem.getModelObject();
            message.markRendered();
            final IModel<String> replacementModel = new Model<String>() {

                private static final long serialVersionUID = 1L;


                /**
                 * Returns feedbackPanel + the message level, eg 'feedbackPanelERROR'. This is used as the class of the
                 * li / span elements.
                 * 
                 * @see org.apache.wicket.model.IModel#getObject()
                 */
                @Override
                public String getObject() {
                    return getCSSClass(message);
                }
            };

            final Component label = newMessageDisplayComponent("message", message);
            final AttributeModifier levelModifier = new AttributeModifier("class", replacementModel);
            //			label.add(levelModifier);
            listItem.add(levelModifier);
            listItem.add(label);
        }
    }


    /** id. */
    private static final long serialVersionUID = 1L;

    /** Message view. */
    private final MessageListView messageListView;


    /**
     * Constructor.
     * 
     * @see org.apache.wicket.Component#Component(String)
     * @param id
     *            id
     */
    public MyFeedbackPanel(final String id) {
        this(id, null);
    }


    /**
     * Constructor.
     * 
     * @see org.apache.wicket.Component#Component(String)
     * 
     * @param id
     *            id
     * @param filter
     *            filter
     */
    public MyFeedbackPanel(final String id, IFeedbackMessageFilter filter) {
        super(id);
        setEscapeModelStrings(false);
        WebMarkupContainer messagesContainer = new WebMarkupContainer("feedbackul") {

            private static final long serialVersionUID = 1L;


            @Override
            public boolean isVisible() {
                return anyMessage();
            }
        };
        add(messagesContainer);
        messageListView = new MessageListView("messages");
        messageListView.setVersioned(false);
        messagesContainer.add(messageListView);

        if (filter != null) {
            setFilter(filter);
        }
    }


    /**
     * Search messages that this panel will render, and see if there is any message of level ERROR or up. This is a
     * convenience method; same as calling 'anyMessage(FeedbackMessage.ERROR)'.
     * 
     * @return whether there is any message for this panel of level ERROR or up
     */
    public final boolean anyErrorMessage() {
        return anyMessage(FeedbackMessage.ERROR);
    }


    /**
     * Search messages that this panel will render, and see if there is any message.
     * 
     * @return whether there is any message for this panel
     */
    public final boolean anyMessage() {
        return anyMessage(FeedbackMessage.UNDEFINED);
    }


    /**
     * Search messages that this panel will render, and see if there is any message of the given level.
     * 
     * @param level
     *            the level, see FeedbackMessage
     * @return whether there is any message for this panel of the given level
     */
    public final boolean anyMessage(int level) {
        List<FeedbackMessage> msgs = getCurrentMessages();

        for (FeedbackMessage msg : msgs) {
            if (msg.isLevel(level)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns model for feedback messages on which you can install filters and other properties.
     * 
     * @return Model for feedback messages on which you can install filters and other properties
     */
    public final FeedbackMessagesModel getFeedbackMessagesModel() {
        return (FeedbackMessagesModel) messageListView.getDefaultModel();
    }


    /**
     * Returns the current message filter.
     * 
     * @return The current message filter
     */
    public final IFeedbackMessageFilter getFilter() {
        return getFeedbackMessagesModel().getFilter();
    }


    /**
     * Returns the current sorting comparator.
     * 
     * @return The current sorting comparator
     */
    public final Comparator<FeedbackMessage> getSortingComparator() {
        return getFeedbackMessagesModel().getSortingComparator();
    }


    @Override
    public boolean isVersioned() {
        return false;
    }


    /**
     * Sets a filter to use on the feedback messages model.
     * 
     * @param filter
     *            The message filter to install on the feedback messages model
     * 
     * @return FeedbackPanel this.
     */
    public final MyFeedbackPanel setFilter(IFeedbackMessageFilter filter) {
        getFeedbackMessagesModel().setFilter(filter);
        return this;
    }


    /**
     * Sets the maximum number of feedback messages that this feedback panel should show at one time.
     * 
     * @param maxMessages
     *            The maximum number of feedback messages that this feedback panel should show at one time
     * 
     * @return FeedbackPanel this.
     */
    public final MyFeedbackPanel setMaxMessages(int maxMessages) {
        messageListView.setViewSize(maxMessages);
        return this;
    }


    /**
     * Sets the comparator used for sorting the messages.
     * 
     * @param sortingComparator
     *            comparator used for sorting the messages.
     * 
     * @return FeedbackPanel this.
     */
    public final MyFeedbackPanel setSortingComparator(Comparator<FeedbackMessage> sortingComparator) {
        getFeedbackMessagesModel().setSortingComparator(sortingComparator);
        return this;
    }


    /**
     * Gets the css class for the given message.
     * 
     * @param message
     *            the message
     * @return the css class; by default, this returns feedbackPanel + the message level, eg 'feedbackPanelERROR', but
     *         you can override this method to provide your own
     */
    protected String getCSSClass(final FeedbackMessage message) {
        switch (message.getLevel()) {
            case FeedbackMessage.DEBUG:
            case FeedbackMessage.WARNING:
                return "alert alert-warning";
            case FeedbackMessage.ERROR:
            case FeedbackMessage.FATAL:
                return "alert alert-error";
            case FeedbackMessage.INFO:
                return "alert alert-info";
            case FeedbackMessage.SUCCESS:
                return "alert alert-success";
            default:
                return "alert alert-warning";
        }
    }


    /**
     * Gets the currently collected messages for this panel.
     * 
     * @return the currently collected messages for this panel, possibly empty
     */
    protected final List<FeedbackMessage> getCurrentMessages() {
        final List<FeedbackMessage> messages = messageListView.getModelObject();
        return Collections.unmodifiableList(messages);
    }


    /**
     * Gets a new instance of FeedbackMessagesModel to use.
     * 
     * @return Instance of FeedbackMessagesModel to use
     */
    protected FeedbackMessagesModel newFeedbackMessagesModel() {
        return new FeedbackMessagesModel(this);
    }


    /**
     * Generates a component that is used to display the message inside the feedback panel. This component must handle
     * being attached to <code>span</code> tags.
     * 
     * By default a {@link Label} is used.
     * 
     * Note that the created component is expected to respect feedback panel's {@link #getEscapeModelStrings()} value
     * 
     * @param id
     *            parent id
     * @param message
     *            feedback message
     * @return component used to display the message
     */
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        Serializable serializable = message.getMessage();
        Label label = new Label(id, (serializable == null) ? "" : serializable.toString());
        label.setEscapeModelStrings(MyFeedbackPanel.this.getEscapeModelStrings());
        return label;
    }


    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof FeedbackEvent) {
            ((FeedbackEvent) event.getPayload()).getTarget().add(this);
        }
    }
}
