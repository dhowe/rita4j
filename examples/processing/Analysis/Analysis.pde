import rita.*;

String pos="", word="", phones;
int last = -9999, maxWordLength = 12, colors[];
Bubble[] bubbles = new Bubble[maxWordLength];

void setup()
{
  size(600, 300);
  textFont(createFont("Georgia", 36));
  noStroke();

  colors = colorGradient();
  for (int i = 0; i < bubbles.length; i++) {
    bubbles[i] = new Bubble();
  }
}


void draw()
{
  background(255);

  fill(56, 66, 90);
  textSize(36);
  textAlign(LEFT);
  text(word, 80, 50);

  textSize(18);
  text("/"+Util.arpaToIPA(phones)+ "/", 80, 80);

  textSize(14);
  text(pos.toUpperCase(), 80, 105);

  for (int i = 0; i < bubbles.length; i++) {
    bubbles[i].draw(i);
  }

  int now = millis();
  if (now - last > 4000) { // timer
    last = now;
    nextWord();
  }
}


void nextWord() { // every 4 sec

  // random word with <= 12 letters
  do {
    word = RiTa.randomWord();
  } while (word.length() > maxWordLength);

  // get various features
  String sy = RiTa.syllables(word);
  String ss = RiTa.stresses(word);
  phones = RiTa.phones(word);

  // get (WordNet-style) pos-tag
  pos = tagName(RiTa.pos(word, true)[0]);

  // update the bubbles
  String[] phs = phones.split("-");
  for (int i = 0; i < phs.length; i++) {
    bubbles[i].update(phs[i], i*50+100);
  }

  addStresses(ss, sy, bubbles);
  addSyllables(sy, bubbles);
}
