
import java.util.StringTokenizer;
import ru.jimbot.util.*;

 /*
  * @author fraer72
  * �����������
  */

  try {
  Integer maxCnt = 3; // ����������� ����� ���� � ���������
  Integer Cnt = 0;
  char[] number_0 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
  String[] number_1 = {"����", "����", "���", "���", "������", "����", "�����", "����", "������", "������"};
  String delimiters = " \t\n\r,.";

  StringTokenizer st = new StringTokenizer(msg, delimiters);

   while(st.hasMoreTokens()){  // ���������� ���������
   String s = st.nextToken();
     for (int i = 0 ;i < number_0.length; i++){
	   if(s.indexOf(number_0[i]) != -1 ||
               s.indexOf(number_1[i]) != -1) 
			   Cnt++;
	     if(Cnt > maxCnt) 
		 msg = msg.replace(s, "*"); // ���� ���� �������������, �������
	 }
   }

   } catch (Exception ex) {
   ex.printStackTrace();
	Log.getLogger(srv.getName()).info(ex.getMessage().toString());      
	 }
   