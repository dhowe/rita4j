import rita.*;

int last = -9999;
Word[] words;
String txt = "Last Wednesday we decided to visit the zoo. We arrived the next morning after we breakfasted, cashed in our passes and entered. We walked toward the first exhibits. I looked up at a giraffe as it stared back at me. I stepped nervously to the next area. One of the lions gazed at me as he lazed in the shade while the others napped. One of my friends first knocked then banged on the tempered glass in front of the monkey's cage. They howled and screamed at us as we hurried to another exhibit where we stopped and gawked at plumed birds. After we rested, we headed for the petting zoo where we petted wooly sheep who only glanced at us but the goats butted each other and nipped our clothes when we ventured too near their closed pen. Later, our tired group nudged their way through the crowded paths and exited the turnstiled gate. Our car bumped, jerked and swayed as we dozed during the relaxed ride home.";

void setup()
{
  size(600, 400);
  words = createWords(txt,  50, 30, 500, 20);
}

void draw() {
  fill(0);
  textSize(16);
  textLeading(20);
  background(250);

  for (int i = 0; i < words.length; i++) {
    words[i].draw();
  }
  
  int now = millis();
  if (now - last > 2000) {
    last = now;
    nextWord();
  }
}

//  replace a random word in the paragraph every 2 sec
void nextWord()
{   
  // loop from a random spot
  int count = (int)random(0, words.length);
  for (int i = count; i < words.length; i++) 
  {
    // only words of 3 or more chars
    if (words[i].txt.length() < 3) continue;

    String pos = RiTa.tagger.allTags(words[i].toLowerCase())[0];  

    if (pos != null) 
    {
      // get the synset
      String[] syns = RiTa.rhymes(words[i]);

      // only words with >1 rhymes
      if (syns.length<2) continue;

      // pick a random rhyme
      int randIdx = (int)random(0, syns.length);
      String newStr = syns[randIdx];

      if (Character.isUpperCase(words[i].charAt(0))) {             
        newStr = RiTa.capitalize(newStr); // keep capitals
      }

      //println("replace: "+words[i]+" -> "+newStr);

      // and make a substitution
      text = text.replaceAll("\\b"+words[i]+"\\b", newStr);

      break;
    }
  }
}    

Word[] createWords(String txt, float tx, float ty, float tw, float lead) {
  String[] strs = txt.split(" ");
  Word[] words = new Word[strs.length];
  words[0] = new Word(strs[0], tx, ty);
  for (int i = 1; i < strs.length; i++) {
    float x = words[i-1].x + textWidth(strs[i-1]+' ');
    float y = words[i-1].y;
    if (i < words.length -1) {
      float nw = textWidth(strs[i]);
      if (x > (words[0].x + tw) - nw) {
        x = words[0].x;
        y += lead;
      }
    }
    words[i] = new Word(strs[i], x, y);
  }
  return words;
}

class Word {
  float x, y, w, h, d;
  boolean bound;
  String txt;
  float[] oc, c;
  int ts;
  Word(String s, float tx, float ty) {
    x = tx;
    y = ty;
    txt = s;
    w = textWidth(txt);
    d = textDescent();
    h = textAscent()+d;
    oc = colors[int(random(colors.length))];
    c = new float[]{oc[0],oc[1],oc[2],oc[3]};
  }
  boolean contains(float mx, float my) {
    return mx >= x && mx <= x+w && my <= y+d && my >= y-h;
  }
  void hilite() {
    c = new float[]{oc[0],oc[1],oc[2],oc[3]};
    ts = millis();
  }
  void draw() {
    noStroke();
    fill(c[0],c[1],c[2],c[3]);
    rect(x, y+d, w, -h);
    fill(0);
    text(txt, x, y);
    c[3] = lerp(c[3],0,(millis()-ts)/100000.0);
  }
}

float[][] colors = {
  new float[] {172, 180, 200, 70}, 
  new float[] {192, 178, 194, 100}, 
  new float[] {186, 180, 187, 70}, 
  new float[] {211, 219, 231, 150}, 
  new float[] {249, 238, 243, 255}, //pink
  new float[] {199, 198, 206, 100}, 
};
