import rita.*;
import java.util.Arrays;

String txt = "Last Wednesday we decided to visit the zoo. We arrived the next morning after we breakfasted, cashed in our passes and entered. We walked toward the first exhibits. I looked up at a giraffe as it stared back at me. I stepped nervously to the next area. One of the lions gazed at me as he lazed in the shade while the others napped. One of my friends first knocked then banged on the tempered glass in front of the monkey's cage. They howled and screamed at us as we hurried to another exhibit where we stopped and gawked at plumed birds. After we rested, we headed for the petting zoo where we petted wooly sheep who only glanced at us but the goats butted each other and nipped our clothes when we ventured too near their closed pen. Later, our tired group nudged their way through the crowded paths and exited the turnstiled gate. Our car bumped, jerked and swayed as we dozed during the relaxed ride home.";
int last = -9999;
Word[] words;

void setup()
{
  size(600, 400);
  textSize(16);
  textLeading(20);
  words = createWords(txt, 50, 30, 500, 20);
}

void draw() {

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
    String word = words[i].text;
      
    // only words of 3 or more chars
    if (word.length() < 3) continue;
    
    String fpos = RiTa.tagger.allTags(word.toLowerCase())[0];  

    if (fpos != null) 
    {
      // get the synset
      String[] syns = RiTa.rhymes(word, RiTa.opts("pos", fpos));

      // only words with >1 rhymes
      if (syns.length<2) continue;

      // pick a random rhyme
      int randIdx = (int)random(0, syns.length);
      String next = syns[randIdx];

      if (Character.isUpperCase(word.charAt(0))) {             
        next = RiTa.capitalize(next); // keep capitals
      }

      println("replace: "+word+" -> "+next);

      // and make a substitution
      words[i].text = next;
      break;
    }
  }
  words = createWords(wordsToString(), 50, 30, 500, 20);
}

String wordsToString() {
  String[] s = new String[words.length];
  for (int i = 0; i < s.length; i++) {
    s[i] = words[i].text;
  }
  return RiTa.untokenize(s);
}


Word[] createWords(String txt, float tx, float ty, float tw, float lead) {
  float spcw = textWidth(" ");
  String[] strs = RiTa.tokenize(txt);
  Word[] words = new Word[strs.length];
  words[0] = new Word(strs[0], tx, ty);
  for (int i = 1; i < strs.length; i++) {
    float x = words[i-1].x + textWidth(strs[i-1]);
    if (!RiTa.isPunctuation(strs[i])) x+= spcw;
    float y = words[i-1].y;
    if (i < words.length -1) {
      float nw = textWidth(strs[i]);
      if (x > (tx + tw) - nw) {
        y += lead;
        x = tx;
      }
    }
    words[i] = new Word(strs[i], x, y);
  }
  return words;
}

class Word {
  float x, y, w, h, d;
  boolean bound;
  String text;
  float[] oc, c;
  int ts;
  Word(String s, float tx, float ty) {
    x = tx;
    y = ty;
    text = s;
    w = textWidth(text);
    d = textDescent();
    h = textAscent()+d;
    oc = colors[int(random(colors.length))];
    c = new float[]{oc[0], oc[1], oc[2], oc[3]};
  }
  void set(String newText) {
    text = newText;
    //hilite();
  }
  boolean contains(float mx, float my) {
    return mx >= x && mx <= x+w && my <= y+d && my >= y-h;
  }
  void hilite() {
    c = new float[]{oc[0], oc[1], oc[2], oc[3]};
    ts = millis();
  }
  void draw() {
    noStroke();
    //fill(c[0], c[1], c[2], c[3]);
    //rect(x, y+d, w, -h);
    fill(0);
    text(text, x, y);
    //c[3] = lerp(c[3], 0, (millis()-ts)/100000.0);
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
