
WORK_DIR=/tmp/.antlr
GRAMMAR_DIR=../ritajs/grammar
DEPENDS_DIR=./target/dependency
OUTPUT_DIR=./src/main/java/rita/antlr

if [[ ! -d $GRAMMAR_DIR ]] ; then
  echo \$GRAMMAR_DIR: \'$GRAMMAR_DIR\' not found, aborting
  exit
fi

if [[ ! -f $GRAMMAR_DIR/RiScript.g4 ]] ; then
  echo GRAMMAR_FILE: \'$GRAMMAR_DIR/RiScript.g4\' not found in \'$GRAMMAR_DIR\', aborting
  exit
fi

if [[ ! -d $OUTPUT_DIR ]] ; then
  mkdir $OUTPUT_DIR
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

CP="$DEPENDS_DIR/*:$CLASSPATH"
#echo "$CP"

rm -rf $OUTPUT_DIR/*

java -Xmx500M -cp "$CP" org.antlr.v4.Tool -Dlanguage=Java -lib $GRAMMAR_DIR -o $WORK_DIR -visitor -Xexact-output-dir -package rita.antlr $GRAMMAR_DIR/RiScript.g4

cp $WORK_DIR/*.java $OUTPUT_DIR
rm -rf $WORK_DIR

echo
echo 'Compiled:'
ls $OUTPUT_DIR
