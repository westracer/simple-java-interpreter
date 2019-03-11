package src;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Scanner {
	static final int MAXLEX = 100;
	static final int CONST = 5;
	static final int MAXCONST = 10;
	static final int MAXTEXT = 10000;
	
	final char[] TLex = new char[MAXLEX];

	static final String[] KEYWORDS = 
		new String[]{"for","int","__int64","main","typedef"};
	static final Types[] KEYWORDSTYPES = 
			new Types[]{Types.Tfor, Types.Tint, Types.Tint64, Types.Tmain, Types.Ttypedef};
	
	char[] text;
	int pos_text, num_str = 1, pos_str;
	
	int saved[] = new int[3];
	
	public char[] getLex() {
		return TLex.clone();
	}
	
	public int[] SavePos() {
		saved[0] = pos_text;
		saved[1] = num_str;
		saved[2] = pos_str;
		
		return saved.clone();
	}
	
	public void LoadPos() {
		pos_text = saved[0];
		num_str = saved[1];
		pos_str = saved[2];
	}
	
	public void LoadPos(int[] pos) {
		pos_text = pos[0];
		num_str = pos[1];
		pos_str = pos[2];
	}
	
	public void OpenFile() throws IOException {
        try {
            FileReader fileReader = 
                new FileReader("input.txt");
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
    		int c = 0;
    		ArrayList<Character> charslist = new ArrayList<Character>();
    		while ( (c = bufferedReader.read()) != -1)
    			if ((char) c != '\r')
    				charslist.add((char) c);
    		charslist.add('\0');

    		int len = charslist.size();
    		text = new char[len];
    		for (int i = 0; i < charslist.size(); i++)
    			text[i] = charslist.get(i);
    		
    		bufferedReader.close();
        }
        catch(FileNotFoundException ex) {}
	}
	
	static public String TrimChars(char[] c) {
		int end = 0;
		for (int i = 0; i < c.length; i++)
			if (c[i] == '\0') {
				end = i;
				break;
			}
		return new String(c).substring(0, end);
	}
	
	private Types KeyWord(char[] c) {
		for (int i = 0; i < CONST; i++)
			if (KEYWORDS[i].equalsIgnoreCase(TrimChars(c)))
				return KEYWORDSTYPES[i];
//		System.out.printf("%d,%d: Неизвестное слово: %s\n", num_str, pos_str - 1, TrimChars(c));
//		return Types.Terr;
		return Types.Tid;
	}
	
	public Types Scan() throws IOException {
		Arrays.fill(TLex, '\0');
			
		int i = 0;
		while (text[pos_text] != '\0') {
			if ((text[pos_text] == '\n')) {
				num_str++;
				pos_text++;
	            pos_str=0;
			} else if (text[pos_text] == '/') {
				if (text[pos_text + 1] != '\0')
				        if (text[pos_text + 1] == '/') {
					pos_text++;
					pos_str++;
					while (text[++pos_text] != '\0' && text[pos_text] != '\n')
					            pos_str++;
				} else if (text[pos_text + 1] == '*') {
					pos_text += 3;
					pos_str += 3;
					if (text[pos_text - 1] == '\n') {
						num_str++;
						pos_str = 0;
					}
					while (text[pos_text] != '\0') {
						if (text[pos_text - 1] == '*' && text[pos_text] == '/') {
							pos_text++;
							break;
						}
						if (text[pos_text] == '\n') {
							num_str++;
							pos_str = 0;
						}
						pos_text++;
					}
				} else break; else {
					pos_text++;
					pos_str++;
				}
			} else if (text[pos_text] == '\t' || text[pos_text] == ' ') {
				pos_text++;
				pos_str++;
			} else break;
		}
		// пропуск игнорируемых символов
		if (text[pos_text] == '\0') {
			TLex[0] = '\0';
			return Types.Tend;
		} else {
			if (Character.isAlphabetic(text[pos_text]) || text[pos_text] == '_') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				while (Character.isDigit(text[pos_text]) || Character.isAlphabetic(text[pos_text]) || text[pos_text] == '_') {
					if (i == MAXLEX - 1) {
						System.out.printf("%d,%d: Длинная лексема\n", num_str, pos_str - 1);
						return Types.Terr;
					}
					TLex[i++] = text[pos_text++];
					pos_str++;
				}
				return KeyWord(TLex);
			}
			//скобки 
			else if (text[pos_text] == '(') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.TlPar;
			} else if (text[pos_text] == ')') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.TrPar;
			} else if (text[pos_text] == '{') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.TlBraces;
			} else if (text[pos_text] == '}') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.TrBraces;
			} else if (text[pos_text] == '[') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.TlBrackets;
			} else if (text[pos_text] == ']') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.TrBrackets;
			}
			//константы
			else if (Character.isDigit(text[pos_text])) {
				if (text[pos_text + 1] == 'x' && (Character.isDigit(text[pos_text+2]) ||
			            (text[pos_text + 2] >= 'a' && text[pos_text + 2] <= 'f') ||
			            (text[pos_text + 2] >= 'A' && text[pos_text + 2] <= 'F'))) {
					TLex[i++] = text[pos_text++];
					TLex[i++] = text[pos_text++];
					pos_str++;
					while (Character.isDigit(text[pos_text]) ||
					            text[pos_text] >= 'a' && text[pos_text] <= 'f' ||
					            text[pos_text] >= 'A' && text[pos_text] <= 'F') {
						if (i < MAXLEX - 1) {
							TLex[i++] = text[pos_text];
							pos_str++;
						}
						pos_text++;
					}
					if (i > MAXCONST) {
						System.out.printf("%d,%d: Длинная константа\n", num_str, pos_str - 1);
						return Types.Terr;
						//длинная константа
					} else return Types.Tc16int;
				}
				else if (Character.isDigit(text[pos_text])) //десятичные 
					{
					TLex[i++] = text[pos_text++];
					pos_str++;
					while (Character.isDigit(text[pos_text])) {
						if (i < MAXLEX - 1) {
							TLex[i++] = text[pos_text];
							pos_str++;
						}
						pos_text++;
					}
					if (i > MAXCONST) {
						System.out.printf("%d,%d: Длинная константа\n", num_str, pos_str - 1);
						return Types.Terr;
						//длинная константа
					} else return Types.Tc10int;
				}
			}
			//знаки операций 
			else if (text[pos_text] == '*') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.Tmul;
			} else if (text[pos_text] == '/') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.Tdiv;
			} else if (text[pos_text] == '%') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.Tmod;
			} else if (text[pos_text] == '+') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				if (text[pos_text] == '+') {
					TLex[i++] = text[pos_text++];
					pos_str++;
					return Types.Tinc;
				}
				return Types.Tplus;
			} else if (text[pos_text] == '-') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				if (text[pos_text] == '-') {
					TLex[i++] = text[pos_text++];
					pos_str++;
					return Types.Tdec;
				}
				return Types.Tminus;
			} else if (text[pos_text] == '<') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				if (text[pos_text] == '=') {
					TLex[i++] = text[pos_text++];
					pos_str++;
					return Types.TlessEq;
				}
				return Types.Tless;
			} else if (text[pos_text] == '>') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				if (text[pos_text] == '=') {
					TLex[i++] = text[pos_text++];
					pos_str++;
					return Types.TmoreEq;
				}
				return Types.Tmore;
			} else if (text[pos_text] == '!') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				if (text[pos_text] == '=') {
					TLex[i++] = text[pos_text++];
					pos_str++;
					return Types.TnotEq;
				} else {
					System.out.printf("%d,%d: Ошибочный символ\n", num_str, pos_str - 1);
					return Types.Terr;
				}
			} else if (text[pos_text] == '=') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				if (text[pos_text] == '=') {
					TLex[i++] = text[pos_text++];
					pos_str++;
					return Types.Teq;
				}
				return Types.Tas;
			}
			//разделители 
			else if (text[pos_text] == ';') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.Tsem;
			} else if (text[pos_text] == ',') {
				TLex[i++] = text[pos_text++];
				pos_str++;
				return Types.Tcomma;
			} else {
				pos_text++;
				pos_str++;
				System.out.printf("%d,%d: Ошибочный символ\n", num_str, pos_str - 1);
				return Types.Terr;
			}
		}
		return Types.Tend;
	}
}
