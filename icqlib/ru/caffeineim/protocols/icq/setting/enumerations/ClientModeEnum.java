/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.caffeineim.protocols.icq.setting.enumerations;

/**
 *
 * @author fraer72
 */

public class ClientModeEnum {

	public static final int NONE = 0;
	public static final int CAP_MIRANDAIM = 1;
	public static final int CAP_MACICQ = 2;
	public static final int CAP_QIP = 3;
        public static final int CAP_QIP_Infium = 4;
        public static final int CAP_QIP_Mobile = 5;
        public static final int CAP_QIP_PDA_Symbian = 6;
        public static final int CAP_QIP_PDA_Windows = 7;
        public static final int CAP_Licq = 8;
        public static final int CAP_mChat = 9;
        public static final int CAP_mIcq = 10;
        public static final int CAP_SIM = 11;
        public static final int CAP_RQ = 12;
        public static final int CAP_SMAPER = 13;
        public static final int CAP_JIMM_DICHAT = 14;
        public static final int CAP_QUTIM = 15;
        public static final int CAP_ICQ6 = 16;
        public static final int CAP_BAYANICQ = 17;
        public static final int CAP_MAIL_AGENT = 18;
        public static final int CAP_KOPETE = 19;
        //public static final int CAP_JIMM = 20;

private int client;


	/**
	 * Create new ClientModeEnum Instance
	 *
	 * @param status
	 */
	public ClientModeEnum(int client) {
            if(client < 0 || client > 19)
            this.client = 0;
            else
	    this.client = client;
	}

	/**
	 *
	 * @return current Client bytes
	 */
	public byte[] getClientData() {
		return clientMatrix[client];
	}

	/**
	 *
	 * @return current Client
	 */
	public int getClient() {
		return client;
	}
        
        private byte[][] clientMatrix = {
        /*NONE*/
        {    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        /*CAP_MIRANDAIM*/    
        {   (byte) 0x4D, (byte) 0x69,
            (byte) 0x72, (byte) 0x61, (byte) 0x6E, (byte) 0x64, (byte) 0x61,
            (byte) 0x4D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }, 
        /*CAP_MACICQ*/    
        {   (byte) 0xdd, (byte) 0x16,
            (byte) 0xf2, (byte) 0x02, (byte) 0x84, (byte) 0xe6, (byte) 0x11,
            (byte) 0xd4, (byte) 0x90, (byte) 0xdb, (byte) 0x00, (byte) 0x10,
            (byte) 0x4b, (byte) 0x9b, (byte) 0x4b, (byte) 0x7d },
        /*CAP_QIP*/    
        {   0x56, 0x3F, (byte) 0xC8, 0x09, 0x0B, 0x6F, 0x41, 'Q', 'I', 'P', ' ', '2', '0',
                        '0', '5', 'a' },
        /*CAP_QIP_Infium*/
        {   (byte) 0x56, (byte) 0x3F, (byte) 0xC8, (byte) 0x09, (byte) 0x0B, (byte) 0x6F, (byte) 0x41,
            (byte) 0x51, (byte) 0x49, (byte) 0x50, (byte) 0x20, (byte) 0x32,
            (byte) 0x30, (byte) 0x30, (byte) 0x35, (byte) 0x61},
        /*CAP_QIP_Mobile*/
        {   (byte) 0x7C, (byte) 0x73, (byte) 0x75, (byte) 0x02, (byte) 0xC3, (byte) 0xBE, (byte) 0x4F,
            (byte) 0x3E, (byte) 0xA6, (byte) 0x9F, (byte) 0x01, (byte) 0x53, (byte) 0x13, (byte)
            0x43, (byte) 0x1E, (byte) 0x1A},
        /*CAP_QIP_PDA_Symbian*/
        {   (byte) 0x56, (byte) 0x3F, (byte) 0xC8, (byte) 0x09, (byte) 0x0B, (byte) 0x6F, (byte) 0x41,
            (byte) 0x51, (byte) 0x49, (byte) 0x50, (byte) 0x20, (byte) 0x20, (byte) 0x20,
            (byte) 0x20, (byte) 0x20, (byte) 0x22},
        /*CAP_QIP_PDA_Windows*/
        {   (byte) 0x51, (byte) 0xAD, (byte) 0xD1, (byte) 0x90, (byte) 0x72, (byte) 0x04, (byte) 0x47,
            (byte) 0x3D, (byte) 0xA1, (byte) 0xA1, (byte) 0x49, (byte) 0xF4, (byte) 0xA3,
            (byte) 0x97, (byte) 0xA4, (byte) 0x1F},
        /*CAP_Licq*/
        {   'L', 'i', 'c', 'q', ' ', 'c', 'l', 'i', 'e', 'n', 't', ' ',
             (byte) 0, (byte) 0, (byte) 0, (byte) 0 },
        /*CAP_mChat*/
        {   'm', 'C', 'h', 'a', 't', ' ','i', 'c', 'q' },
        /*CAP_mIcq*/
        {   'm', 'I', 'C', 'Q', ' ',(byte) 0xA9, ' ', 'R', '.', 'K', '.', ' ', (byte) 0, (byte) 0,(byte) 0, (byte) 0 },
        /*CAP_SIM*/
        {   'S', 'I', 'M', ' ', 'c', 'l', 'i','e', 'n', 't', ' ', ' ', (byte) 0, (byte) 0, (byte) 0, (byte) 0 },
        /*CAP_RQ*/
        {   '&', 'R', 'Q', 'i', 'n', 's','i', 'd', 'e', (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,(byte) 0, (byte) 0 },
        /*CAP_SMAPER*/
        {    'S', 'm', 'a', 'p', 'e', 'r' },
        /*CAP_JIMM_DICHAT*/
        {    (byte) 0x44, (byte) 0x5B,
             (byte) 0x69, (byte) 0x5D, (byte) 0x43, (byte) 0x68, (byte) 0x61,
             (byte) 0x74, (byte) 0x20 },
        /*CAP_QUTIM*/
        {    'q', 'u', 't', 'i', 'm' },
        /*CAP_ICQ6*/
        {    (byte)0x01, (byte)0x38, (byte)0xca, (byte)0x7b, (byte)0x76, (byte)0x9a, (byte)0x49, (byte)0x15, (byte)0x88, (byte)0xf2,
             (byte)0x13, (byte)0xfc, (byte)0x00, (byte)0x97, (byte)0x9e, (byte)0xa8},
        /*CAP_BAYANICQ*/
        {    'b', 'a', 'y', 'a', 'n', 'I', 'C', 'Q', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        /*CAP_MAIL_AGENT*/
        {    'I', 'c', 'q', 'K', 'i', 'd', '2', (byte)0x00, (byte)0x05, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
              (byte)0x00, (byte)0x00, (byte)0x00},
        /*CAP_KOPETE*/
        {     'K', 'o', 'p', 'e', 't', 'e', ' ', 'I', 'C', 'Q', ' ', ' ', (byte)0, (byte)0, (byte)0, (byte)0},
        /*CAP_JIMM*/
        //{     'J', 'i', 'm', 'm', ' ' }
        };






}
