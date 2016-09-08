package cz.GravelCZLP.Breakpoint.managers;

import cz.GravelCZLP.Breakpoint.language.MessageType;

public class ChatManager
{
	public static String prefixAdmin;
	public static String prefixModerator;
	public static String prefixHelper;
	public static String prefixVIP;
	public static String prefixYT;
	public static String prefixSponsor;
	public static String tagPrefixVIP;
	public static String tagPrefixYT;
	public static String tagPrefixSponsor;
	public static String prefixDeveloper;
	public static String prefixVIPPlusPlus;
	public static String prefixVIPPlus;
	public static String prefixAdminDev;
	
	public static void loadStrings()
	{
		prefixAdmin = MessageType.CHAT_PREFIX_ADMIN.getTranslation().getValue();
		prefixModerator = MessageType.CHAT_PREFIX_MODERATOR.getTranslation().getValue();
		prefixHelper = MessageType.CHAT_PREFIX_HELPER.getTranslation().getValue();
		prefixYT = MessageType.CHAT_PREFIX_YOUTUBE.getTranslation().getValue();
		prefixSponsor = MessageType.CHAT_PREFIX_SPONSOR.getTranslation().getValue();
		
		prefixDeveloper = MessageType.CHAT_PREFIX_DEV.getTranslation().getValue();
		
		prefixAdminDev = MessageType.CHAT_PREFIX_AdminDev.getTranslation().getValue();
		
		prefixVIPPlus = MessageType.CHAT_PREFIX_VIPPLUS.getTranslation().getValue();
		prefixVIP = MessageType.CHAT_PREFIX_VIP.getTranslation().getValue();
		
		tagPrefixYT = MessageType.CHAT_TAGPREFIX_YOUTUBE.getTranslation().getValue();
		tagPrefixSponsor = MessageType.CHAT_TAGPREFIX_SPONSOR.getTranslation().getValue();
	}
}
