import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindNewWord {
	public boolean if_remove_oldWors = true;// 是否在n-gram阶段就滤掉旧词
	public String str_in="我现在正在玩lol，现在战况激烈可能现在没办法抽空回复你";
	int space_num = 4;
	public int len_max = 4;
	double compactness_t = 1000;
	double flexible_t = 0.5;
	double wcount_t = 5;
	public HashMap<String,String> w_Map = new HashMap<String,String>();
	public HashMap<String,String> nw_Map = new HashMap<String,String>();
	public Set<String> wset = new HashSet<String>();
	public int str_len = str_in.length();
	public int game_id;
	public String date_time;
	//
	public static Set<String> foundWords = new HashSet<String>();
	public String cm = "99999999999999999";
	public int count_max = Integer.parseInt(cm.substring(0,space_num));
	//
	// 开始统计词信息
	public void wordStatics(){
		str_in = str_in.replaceAll("[^\u4E00-\u9FA5^a-z^A-Z^0-9^\\>]", "");//[^a-z^A-Z^0-9]
		str_len = str_in.length();
		if(str_len<2) return;
		
		// 初始化，减少包含检索
		String spacel = "                                  ";
		String info_ini = 0+",的:0"+spacel.substring(0,space_num-1)+",的:0"+spacel.substring(0,space_num-1);
		// n-gram (n=len_max)
		for(int i=0;i<str_len;i++){
			if(str_in.charAt(i)=='>') continue;
			int flag = 0;//判断是否遇到句尾
			for(int j=0;j<=len_max;j++){
				if(i+j+1>str_len) continue;
				String cur_w = str_in.substring(i, i+j+1);//当前词
				if(cur_w.charAt(cur_w.length()-1)=='>'){//当前词末尾已经是完结符,则其不构成一个词，不用统计
					flag = 1;
				}
				if(flag == 1) continue;
				// 初始化
				wset.add(cur_w);
				w_Map.put(cur_w, info_ini);
			}
		}
			
		// 开始统计
		for(int i=0;i<str_in.length();i++){
			if(str_in.charAt(i)=='>') continue;
			int flag = 0;//判断是否遇到句尾 or 是旧词
			for(int j=0;j<=len_max;j++){
				if(i+j+1>str_len) continue;
				String cur_w = str_in.substring(i, i+j+1);//当前词
				if(cur_w.charAt(cur_w.length()-1)=='>'){//当前词末尾已经是完结符
					flag = 1;
				}
				if(flag == 1) continue;
				//
				String info_all = w_Map.get(cur_w);
				String info[] = info_all.split(",");
				int num = Integer.parseInt(info[0])+1;
				//
				String left_w = "";
				if(i!=0 && str_in.charAt(i-1)!='>')  left_w += str_in.charAt(i-1);
				String right_w = "";
				if((i+j+1)<str_len && str_in.charAt(i+j+1)!='>')  right_w += str_in.charAt(i+j+1);
				//
				info_all = updateWordStatic(info_all, left_w, "left");
				info_all = updateWordStatic(info_all, right_w, "right");
				info_all = info_all.substring((info_all.indexOf(',', 0)));
				w_Map.put(cur_w, num+info_all);
				if(cur_w.equals("现在")){
					//System.out.println(cur_w+"==>:"+num+","+left_w+","+right_w);
				}
			}
		}
	}
	// 更新词统计信息
	public String updateWordStatic(String str,String w_cur,String place){
		if(w_cur.length()<1) return str;
		String[] str_l = str.split(",");
		String spacel = "                                  ";
		if(place.equals("left")){
			int i_w = str_l[1].indexOf(w_cur+":");
			//如果找到
			if(i_w>0){
				int i_n = i_w + w_cur.length()+1;
				int i_b = i_n+space_num;
				
				int num_cur = Integer.parseInt(str_l[1].substring(i_n, i_n+space_num).replaceAll(" ", ""))+1;
				String nums = "                           ";
				nums = nums.substring(0,space_num);
				if(num_cur<count_max){
					String num_t = num_cur +  "";
					nums = num_t + nums.substring(num_t.length());
				}
				else num_cur=count_max;
				str = str_l[0] + ","+str_l[1].substring(0, i_w) 
						+ w_cur+":"+nums
						+str_l[1].substring(i_b)+","+str_l[2];
			}
			// 如果没有
			else{
				
				str = str_l[0] + ","+str_l[1]+w_cur+":"+"1"+spacel.substring(0,space_num-1)
						+","+str_l[2];
			}
		}
		if(place.equals("right")){
			int i_w = str_l[2].indexOf(w_cur+":");
			//如果找到
			if(i_w>0){
				int i_n = i_w + w_cur.length()+1;
				int i_b = i_n+space_num;
				
				int num_cur = Integer.parseInt(str_l[2].substring(i_n, i_n+space_num).replaceAll(" ", ""))+1;
				String nums = "                           ";
				nums = nums.substring(0,space_num);
				if(num_cur<count_max){
					String num_t = num_cur +  "";
					nums = num_t + nums.substring(num_t.length());
				}
				else num_cur=count_max;
				str = str_l[0] + ","+str_l[1]+","+str_l[2].substring(0, i_w) 
						+ w_cur+":"+nums
						+str_l[2].substring(i_b);
			}
			// 如果没有
			else{
				str = str_l[0] + ","+str_l[1]
						+","+str_l[2]+w_cur+":"+"1"+spacel.substring(0,space_num-1);
			}
		}
		return str;
	}
	// 计算词内凝固度、词外自由度 发现新词
	public HashMap<String,String> findWords() throws Exception{
		System.out.println("existing word num: "+foundWords.size());
		//
		for(String s:wset){
			if(s.length()>1 && s.replaceAll("[^\u4E00-\u9FA5]", "").length()>0){//[^a-z^A-Z^0-9]){
				// 计算词内凝固度
				String info[] = w_Map.get(s).split(",");
				double compact_score = Double.MAX_VALUE;
				Integer s0 = Integer.parseInt(info[0]);//词频
				for(int i=0;i<s.length()-1;i++){
					// 计算词内凝固度
					//如果能分成两半
					if(wset.contains(s.substring(0, i+1)) & wset.contains(s.substring(i+1))){
						double s1 = 1.0*Integer.parseInt(w_Map.get(s.substring(0, i+1)).split(",")[0]);//词频
						double s2 = 1.0*Integer.parseInt(w_Map.get(s.substring(i+1)).split(",")[0]);//词频
						double score_comp = s0*str_len*1.0/(s1*s2);//分母是随机组合的概率,比值越大越是人组的语意的词
						//
						compact_score = Math.min(compact_score, score_comp );
					}
				}
				
				// 计算词外自由度
				double left_f = culWordsEtropy(info[1]);
				double right_f =culWordsEtropy(info[2]);
				double flexible = Math.min(left_f, right_f );
				
				// 如果满足条件就加入潜在新词
				if(compact_score>compactness_t 
						&& flexible>flexible_t 
						&& s0>wcount_t 
						&& (!foundWords.contains(s)))
					nw_Map.put(s, s0+","+compact_score+","+flexible);
			}
		}
		return nw_Map;
	}
	
	// 计算词熵
	public double culWordsEtropy(String str){
		int sumc = 0;
		List<Integer> num_l =  new ArrayList<Integer>();
		for(int i=0;i<str.length();i++){
			if(str.charAt(i)==':'){
				Integer temp = Integer.parseInt(str.substring(i+1, i+5).replaceAll(" ", ""));
				if(temp>0){
					num_l.add(temp);
					sumc += temp;
				}
			}
		}
		//
		double strEtropy = 0.0;
		for(Integer num:num_l){
			strEtropy += (-1.0) * (num*1.0/(sumc*1.0)) * (Math.log(num*1.0/(sumc*1.0)));
		}
		return strEtropy;
	}
	public static void saveStrCSV(String file, String conent) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			out.write(conent+"\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String args[]) throws Exception{ 
		String dataPath = "./data/金瓶梅.txt";//西游记
		//
		InputStreamReader read = new InputStreamReader(new FileInputStream(dataPath),"gbk"); //,"gbk","UTF-8"
		@SuppressWarnings("resource")
		BufferedReader currBR = new BufferedReader(read);
        String tempCurrLine = "";
        String con = "";
        while ((tempCurrLine = currBR.readLine()) != null) {
        	con +=tempCurrLine;
        }
        //
        FindNewWord fnw = new FindNewWord();
		fnw.str_in = con;
		fnw.wordStatics();
		HashMap<String,String> fw = fnw.findWords();
		System.out.println("\n\n\nnew words num: " + fw.size()); 
		//
		List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(fw.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, String>>() 
				{
					@Override
					public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
						Integer num1 = Integer.parseInt(o1.getValue().split(",")[0]);
						Integer num2 = Integer.parseInt(o2.getValue().split(",")[0]);
						Double com1 = Double.valueOf(o1.getValue().split(",")[1]);
						Double com2 = Double.parseDouble(o2.getValue().split(",")[1]);
						//return com2.compareTo(com1);
						return num2.compareTo(num1);
					}
				});
		for (int i = 0; i < 300; ++i) {
			System.out.println(entryList.get(i).getKey()+"\t-->"+ entryList.get(i).getValue());
		}
	}
}
