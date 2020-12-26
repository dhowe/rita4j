import rita.*;

String[] words ="Last Wednesday we decided to visit the zoo. We arrived the next morning after we breakfasted, cashed in our passes and entered. We walked toward the first exhibits. I looked up at a giraffe as it stared back at me. I stepped nervously to the next area. One of the lions gazed at me as he lazed in the shade while the others napped. One of my friends first knocked then banged on the tempered glass in front of the monkey's cage. They howled and screamed at us as we hurried to another exhibit where we stopped and gawked at plumed birds. After we rested, we headed for the petting zoo where we petted wooly sheep who only glanced at us but the goats butted each other and nipped our clothes when we ventured too near their closed pen. Later, our tired group nudged their way through the crowded paths and exited the turnstiled gate. Our car bumped, jerked and swayed as we dozed during the relaxed ride home.".split(" ");
float[] x = new float[words.length], y = new float[words.length];

void setup()
{
  size(600, 400);   
  fill(0);
  textSize(16);
  textLeading(20);

  x[0] = 50;
  y[0] = 30; 
  int w = 500;
  float lead = textAscent() * 1.5 ;
  for (int i = 1; i < words.length; i++) {
    //text(x, x, y+20);
    x[i] = x[i-1] + textWidth(words[i-1]+' ');
    y[i] = y[i-1];
    if (i < words.length -1) {
      float nw = textWidth(words[i]);
      if (x[i] > (x[0] + w) - nw) {
        x[i] = x[0];
        y[i] += lead;
      }
    }
  }
}

void draw() {
  background(250);
  for (int i = 0; i < words.length; i++) {
    fill(0);
    text(words[i], x[i], y[i]);
    noFill();
    stroke(0);
    rect(x[i], y[i]+textDescent(), textWidth(words[i]), -(textAscent()+textDescent()));
  }
}
