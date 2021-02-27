
WORK_DIR=/tmp/.antlr
GRAMMAR_DIR=../ritajs/grammar
DEPENDS_DIR=./target/dependency
OUTPUT_DIR=./src/main/java/rita/antlr

if [[ ! -d $GRAMMAR_DIR ]] ; then
  echo \$GRAMMAR_DIR: \'$GRAMMAR_DIR\' not found, aborting
  exit
fi

if [[ ! -f $GRAMMAR_DIR/RiScriptParser.g4 ]] ; then
  echo GRAMMAR_FILE: \'$GRAMMAR_DIR/RiScriptParser.g4\' not found in \'$GRAMMAR_DIR\', aborting
  exit
fi

if [[ ! -f $GRAMMAR_DIR/RiScriptLexer.g4 ]] ; then
  echo GRAMMAR_FILE: \'$GRAMMAR_DIR/RiScriptLexer.g4\' not found in \'$GRAMMAR_DIR\', aborting
  exit
fi

if [[ ! -d $OUTPUT_DIR ]] ; then
  mkdir $OUTPUT_DIR
  #mkdir $OUTPUT_DIR/antlr
  if [[ ! -d $OUTPUT_DIR ]] ; then
    echo \$OUTPUT_DIR: \'$OUTPUT_DIR\' not found, aborting
    exit
  fi
fi

if [[ ! -d $DEPENDS_DIR ]] ; then
  mvn dependency:copy-dependencies
  if [[ ! -d $DEPENDS_DIR ]] ; then
    echo \$DEPENDS_DIR: \'$DEPENDS_DIR\' not found, aborting
    exit
  fi
fi

CLASSPATH="$DEPENDS_DIR/*:$CLASSPATH"
#echo "$CP"

rm -rf $OUTPUT_DIR/* #$OUTPUT_DIR/antlr/*

# here use sed to copy files from js to java: src/main/java/rita/grammar/
cp $GRAMMAR_DIR/RiScriptParser.g4  $OUTPUT_DIR
sed 's/this.//g; s/\.charCodeAt(0)//g' $GRAMMAR_DIR/RiScriptLexer.g4 > $OUTPUT_DIR/RiScriptLexer.g4

java -Xmx500M -cp "$CLASSPATH" org.antlr.v4.Tool -Dlanguage=Java -lib $OUTPUT_DIR -o $WORK_DIR -visitor -no-listener -Xexact-output-dir -package rita.antlr $OUTPUT_DIR/RiScript*.g4

cp $WORK_DIR/*.java $OUTPUT_DIR
rm -rf $WORK_DIR

echo
echo 'Compiled:'
ls $OUTPUT_DIR
