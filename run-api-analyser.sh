#!/bin/bash
# Run ApiDefinitionAnalyserMain
# Scans a Java source tree for @RestController classes and prints all API endpoints.
#
# Usage:
#   ./run-api-analyser.sh <source-path> [options]
#   ./run-api-analyser.sh --help

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
M2_REPO="${HOME}/.m2/repository"

# Java-Utils system dependency (matches pom.xml systemPath)
JAVA_UTILS_JAR="/Users/paolobrunasti/IdeaProjects/mine/java-utils/target/it.brunasti.java.utils.Utils-1.0.1.jar"
# Fallback to the copy bundled in lib/
if [[ ! -f "$JAVA_UTILS_JAR" ]]; then
    JAVA_UTILS_JAR="${PROJECT_DIR}/lib/Java-Utils-1.0.0.jar"
fi

MAIN_CLASS="it.brunasti.java.diagrammer.spring.api.ApiDefinitionAnalyserMain"

# ---------------------------------------------------------------------------
# Classpath
# ---------------------------------------------------------------------------
CLASSPATH="${PROJECT_DIR}/target/classes"
CLASSPATH="${CLASSPATH}:${JAVA_UTILS_JAR}"
CLASSPATH="${CLASSPATH}:${M2_REPO}/com/thoughtworks/qdox/qdox/2.0.3/qdox-2.0.3.jar"
CLASSPATH="${CLASSPATH}:${M2_REPO}/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar"
CLASSPATH="${CLASSPATH}:${M2_REPO}/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar"
CLASSPATH="${CLASSPATH}:${M2_REPO}/org/apache/logging/log4j/log4j-api/2.25.4/log4j-api-2.25.4.jar"
CLASSPATH="${CLASSPATH}:${M2_REPO}/org/apache/logging/log4j/log4j-core/2.25.4/log4j-core-2.25.4.jar"

# ---------------------------------------------------------------------------
# Usage
# ---------------------------------------------------------------------------
usage() {
    cat <<EOF
Usage: $(basename "$0") [--build] <source-path> [-r] [-o <output-file>] [-c <config-file>] [-d [level]]

  <source-path>     Root directory of the Java source files to analyse (required)
  -r, --recursive   Recursively scan all subdirectories under <source-path>
  -o <file>         Write output to file instead of stdout
  -c <file>         JSON configuration file
  -d [level]        Enable debug output (optional numeric level, e.g. -d 2)
  -h / -?           Show the Java tool's built-in help/usage

Script-level options:
  --build           Run 'mvn compile' before executing (useful after code changes)
  --help            Show this help message

Examples:
  $(basename "$0") /path/to/project/src/main/java/com/example/api
  $(basename "$0") /path/to/project/src/main/java -r -o api-report.txt
  $(basename "$0") /path/to/project/src/main/java -r -o api-report.txt -c config.json
  $(basename "$0") --build /path/to/project/src/main/java -r -d 2 -o api-report.txt
EOF
}

# ---------------------------------------------------------------------------
# Parse script-level flags before forwarding remaining args to Java
# ---------------------------------------------------------------------------
BUILD=false
JAVA_ARGS=()

for arg in "$@"; do
    case "$arg" in
        --help)
            usage
            exit 0
            ;;
        --build)
            BUILD=true
            ;;
        *)
            JAVA_ARGS+=("$arg")
            ;;
    esac
done

if [[ ${#JAVA_ARGS[@]} -eq 0 ]]; then
    usage
    exit 1
fi

# ---------------------------------------------------------------------------
# Optional build step
# ---------------------------------------------------------------------------
if [[ "$BUILD" == true ]]; then
    echo "Building project..."
    mvn compile -f "${PROJECT_DIR}/pom.xml" -q
    if [[ $? -ne 0 ]]; then
        echo "ERROR: Maven build failed." >&2
        exit 1
    fi
    echo "Build successful."
fi

# ---------------------------------------------------------------------------
# Pre-flight checks
# ---------------------------------------------------------------------------
if [[ ! -d "${PROJECT_DIR}/target/classes" ]]; then
    echo "ERROR: target/classes not found. Run with --build or execute 'mvn compile' first." >&2
    exit 1
fi

if [[ ! -f "$JAVA_UTILS_JAR" ]]; then
    echo "ERROR: Java-Utils jar not found at: ${JAVA_UTILS_JAR}" >&2
    echo "       Build the java-utils project or adjust JAVA_UTILS_JAR in this script." >&2
    exit 1
fi

# ---------------------------------------------------------------------------
# Run
# ---------------------------------------------------------------------------
java -cp "${CLASSPATH}" "${MAIN_CLASS}" "${JAVA_ARGS[@]}"
