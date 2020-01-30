package com.aem.community.core.services.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 105876
 *
 */
public class EmailServiceVO {
	/**
	 * template variable key
	 */
	public static final String FROM_ADDRESS = "fromAddress";
	public static final String FROM_NAME = "fromName";
	public static final String TO_NAME = "toName";
	public static final String SUBJECT = "subject";
	public static final String BODY = "body";
	private String concern;
	private String fromAddress;
	private String fromName;
	private List<String> toAddress = new ArrayList<>();
	private List<String> ccAddress = new ArrayList<>();
	private List<String> bccAddress = new ArrayList<>();
	private List<String> replyAddress = new ArrayList<>();
	private String bounceAddress;
	private String charset = "ISO-8859-1";
	private String contentType;
	private List<EmailAttachmentVO> attachment = new ArrayList<>();
	private String toName;
	private Map<String, String> templateVaribles = new HashMap<>();
	private String templatePath;
	private boolean useCQGateway = false;
	private String htmlMessage;
	private String textMessage;
	private boolean hasEmbeddedImage = false;
	private boolean isStartTLS = false;
	private String embeddedImagePath;
	private String embeddedImageDescription;
	private String embeddedContentType;	

	public String getSubject() {
		return concern;
	}

	public void setSubject(String subject) {
		this.concern = subject;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public List<String> getToAddress() {
		return toAddress;
	}

	public void setToAddress(List<String> toAddress) {
		this.toAddress = toAddress;
	}

	public void addToAddress(String toAddress) {
		this.toAddress.add(toAddress);
	}

	public List<String> getCcAddress() {
		return ccAddress;
	}

	public void setCcAddress(List<String> ccAddress) {
		this.ccAddress = ccAddress;
	}

	public void addCcAddress(String ccAddress) {
		this.ccAddress.add(ccAddress);
	}

	public List<String> getBccAddress() {
		return bccAddress;
	}

	public void setBccAddress(List<String> bccAddress) {
		this.bccAddress = bccAddress;
	}

	public void addBccAddress(String bccAddress) {
		this.bccAddress.add(bccAddress);
	}

	public List<String> getReplyAddress() {
		return replyAddress;
	}

	public void setReplyAddress(List<String> replyAddress) {
		this.replyAddress = replyAddress;
	}

	public String getBounceAddress() {
		return bounceAddress;
	}

	public void setBounceAddress(String bounceAddress) {
		this.bounceAddress = bounceAddress;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public List<EmailAttachmentVO> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<EmailAttachmentVO> attachment) {
		this.attachment = attachment;
	}

	public void addAttachment(EmailAttachmentVO attachment) {
		this.attachment.add(attachment);
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public void addTemplateVaribles(String name, String value) {
		this.templateVaribles.put(name, value);
	}

	public Map<String, String> getTemplateVariables() {
		defaultTemplateVariables();
		return templateVaribles;
	}

	/**
	 * @param templateVaribles the templateVaribles to set
	 */
	public void setTemplateVaribles(Map<String, String> templateVaribles) {
		this.templateVaribles = templateVaribles;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath1) {
		this.templatePath = templatePath1;
	}

	public boolean isUseCQGateway() {
		return useCQGateway;
	}

	public void setUseCQGateway(boolean useCQGateway) {
		this.useCQGateway = useCQGateway;
	}	

	// Initialize default variables from bean
	protected void defaultTemplateVariables() {
		templateVaribles.put(FROM_ADDRESS, fromAddress);
		templateVaribles.put(FROM_NAME, fromName);
		templateVaribles.put(TO_NAME, toName);
	}

	public String getHtmlMessage() {
		return htmlMessage;
	}

	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}	

	public boolean hasEmbeddedImage() {
		return hasEmbeddedImage;
	}

	public void setEmbeddedImage(boolean hasEmbeddedImage) {
		this.hasEmbeddedImage = hasEmbeddedImage;
	}

	public boolean isStartTLS() {
		return isStartTLS;
	}

	public void setStartTLS(boolean isStartTLS) {
		this.isStartTLS = isStartTLS;
	}	

	public String getEmbeddedImagePath() {
		return embeddedImagePath;
	}

	public void setEmbeddedImagePath(String embeddedImagePath) {
		this.embeddedImagePath = embeddedImagePath;
	}

	public String getEmbeddedImageDescription() {
		return embeddedImageDescription;
	}

	public void setEmbeddedImageDescription(String embeddedImageDescription) {
		this.embeddedImageDescription = embeddedImageDescription;
	}

	public String getEmbeddedContentType() {
		return embeddedContentType;
	}

	public void setEmbeddedContentType(String embeddedContentType) {
		this.embeddedContentType = embeddedContentType;
	}

	public boolean isValid() {
		return (StringUtils.isNotEmpty(templatePath) && !toAddress.isEmpty());			
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return "EmailServiceVO [" + (concern != null ? "concern=" + concern + ", " : "")
				+ (fromAddress != null ? "fromAddress=" + fromAddress + ", " : "")
				+ (fromName != null ? "fromName=" + fromName + ", " : "")
				+ (toAddress != null ? "toAddress=" + toString(toAddress, maxLen) + ", " : "")
				+ (ccAddress != null ? "ccAddress=" + toString(ccAddress, maxLen) + ", " : "")
				+ (bccAddress != null ? "bccAddress=" + toString(bccAddress, maxLen) + ", " : "")
				+ (replyAddress != null ? "replyAddress=" + toString(replyAddress, maxLen) + ", " : "")
				+ (bounceAddress != null ? "bounceAddress=" + bounceAddress + ", " : "")
				+ (charset != null ? "charset=" + charset + ", " : "")
				+ (contentType != null ? "contentType=" + contentType + ", " : "")
				+ (attachment != null ? "attachment=" + toString(attachment, maxLen) + ", " : "")
				+ (toName != null ? "toName=" + toName + ", " : "")
				+ (templateVaribles != null ? "templateVaribles=" + toString(templateVaribles.entrySet(), maxLen) + ", "
						: "")
				+ (templatePath != null ? "templatePath=" + templatePath + ", " : "") + "useCQGateway=" + useCQGateway
				+ ", " + (htmlMessage != null ? "htmlMessage=" + htmlMessage + ", " : "")
				+ (textMessage != null ? "textMessage=" + textMessage + ", " : "") + "hasEmbeddedImage="
				+ hasEmbeddedImage + ", isStartTLS=" + isStartTLS + ", "
				+ (embeddedImagePath != null ? "embeddedImageURL=" + embeddedImagePath + ", " : "")
				+ (embeddedImageDescription != null ? "embeddedImageDescription=" + embeddedImageDescription + ", "
						: "")
				+ (embeddedContentType != null ? "embeddedContentType=" + embeddedContentType : "") + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}	
}
