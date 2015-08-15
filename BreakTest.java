import java.lang.*;

public class BreakTest{
	public static void main(String[] args){
		int i=0, j=0;
		for(i=0; i<10; i++){
			for(j=0; j<10; j++){
				if (j>5){
					break;
				}
			}
		}
		System.out.println(i + " : " + j);
	}
}