package cz.GravelCZLP.Breakpoint.managers;

import cz.GravelCZLP.Breakpoint.language.MessageType;

public class ChatManager {
	public static String prefixAdmin;
	public static String prefixModerator;
	public static String prefixHelper;
	public static String prefixVIP;
	public static String prefixYT;
	public static String prefixSponsor;;
	public static String prefixDeveloper;
	public static String prefixVIPPlus;
	
	public static String tagPrefixVIP;
	public static String tagPrefixYT;
	public static String tagPrefixSponsor;
	public static String tagPrefixAdmin;
	public static String tagPrefixModerator;
	public static String tagPrefixHelper;
	public static String tagPrefixVIPPlus;
	
	public static void loadStrings() {
		prefixAdmin = MessageType.CHAT_PREFIX_ADMIN.getTranslation().getValue();
		prefixModerator = MessageType.CHAT_PREFIX_MODERATOR.getTranslation().getValue();
		prefixHelper = MessageType.CHAT_PREFIX_HELPER.getTranslation().getValue();
		prefixYT = MessageType.CHAT_PREFIX_YOUTUBE.getTranslation().getValue();
		prefixSponsor = MessageType.CHAT_PREFIX_SPONSOR.getTranslation().getValue();
		prefixVIPPlus = MessageType.CHAT_PREFIX_VIPPLUS.getTranslation().getValue();
		prefixVIP = MessageType.CHAT_PREFIX_VIP.getTranslation().getValue();

		
		tagPrefixYT = MessageType.CHAT_TAGPREFIX_YOUTUBE.getTranslation().getValue();
		tagPrefixSponsor = MessageType.CHAT_TAGPREFIX_SPONSOR.getTranslation().getValue();
		tagPrefixVIP = MessageType.CHAT_TAGPREFIX_VIP.getTranslation().getValue();
		tagPrefixAdmin = MessageType.CHAT_TAGPREFIX_ADMIN.getTranslation().getValue();
		tagPrefixModerator = MessageType.CHAT_TAGPREFIX_MODERATOR.getTranslation().getValue();
		tagPrefixHelper = MessageType.CHAT_TAGPREFIX_HELPER.getTranslation().getValue();
		tagPrefixVIPPlus = MessageType.CHAT_TAGPREFIX_VIPPLUS.getTranslation().getValue();
	}
}
