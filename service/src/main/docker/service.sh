#!/usr/bin/env sh

########################################################################################################################
# This is the central script to start the service via docker cmd
#
# It's behavior can be controlled by environment variables (not command line parameters).
# These environment variables are:
#
# DATASOURCE_URL: declares how the service connects the database
# ALLOW_CORS_ORIGINS: declares origin, where clients can use the service
# ADDITIONAL_PARAMETERS: additional command line parameters for the main class; optional
# JVM_ARGUMENTS: command line parameters for the java VM; optional; default='-XX:MaxRAMPercentage=70 -XX:InitialRAMPercentage=40'
# ADDITIONAL_JVM_ARGUMENTS: additional command line parameters for the java VM (appended to JVM_ARGUMENTS); optional
# ENABLE_HEAPDUMP: controls the creation of a heap dump on OutOfMemory; one of [true,false]; optional; default='false'
# ENABLE_DEBUG: controls the JVM debug options on port 7790; one of [true,suspend,false] (true=enabled, suspend=enabled and suspending, others=disabled); optional; default='false'
# ENABLE_GCLOG: controls the GC log options; one of [true,false] (true=enabled, others=disabled); optional; default='false'
# ENABLE_NEWRELIC: controls if the Newrelic Java agent is enabled; one of [true,false] (true=enabled, others=disabled); optional; default='false'
# NIST_API_KEY: API key for NIST to improve performance for OWASP access

. /docker/bin/functions.sh
  
##############################################################################
# void logAllVars(string varName0[, string varName1 ... [, string varNameN]])
##############################################################################
#
# Logs a list of (known) environment variables.
#
# Example:
#
#   logAllVars
#
# Notice: this function echos messages to stdout.
#
##############################################################################
logAllVars() {
    echo "Supported environment variables are set as follows:"
    echo "DATASOURCE_URL='${DATASOURCE_URL}'"
    echo "ALLOW_CORS_ORIGINS='${ALLOW_CORS_ORIGINS}'"
    echo "JWT_SECRET='${JWT_SECRET}'"
    echo "JVM_ARGUMENTS='${JVM_ARGUMENTS}'"
    echo "ENABLE_HEAPDUMP='${ENABLE_HEAPDUMP}'"
    echo "ENABLE_DEBUG='${ENABLE_DEBUG}'"
    echo "ENABLE_GCLOG='${ENABLE_GCLOG}'"
}

##############################################################################
# END FUNCTIONS
##############################################################################

logAllVars

REQ_VARS="DATASOURCE_URL ALLOW_CORS_ORIGINS JWT_SECRET"
checkAllVars "${REQ_VARS}"

RESULT_CODE="$?"
if [ "$RESULT_CODE" -ne 0 ]; then
    exit $RESULT_CODE
fi

#
# Assemble Java options
#
SERVICE_CLASSPATH=""
EXT_JVM_ARGUMENTS="-XX:MaxRAMPercentage=60 -XX:InitialRAMPercentage=60"

if [ ! -z "$JVM_ARGUMENTS" -a  "$JVM_ARGUMENTS" != "" ]; then
    EXT_JVM_ARGUMENTS=$JVM_ARGUMENTS
fi

JAVA_OPTS=$EXT_JVM_ARGUMENTS

# enable helpful NullPointerException
JAVA_OPTS=$JAVA_OPTS\ -XX:+ShowCodeDetailsInExceptionMessages

if [ ! -z "${ENABLE_HEAPDUMP}" -a "${ENABLE_HEAPDUMP}" = "true" ]; then
    # dump directory to use
    HEAP_DUMPS_PATH="$(serverDirProperty 'IS_DUMPS')"
    # generate a heap dump on OOM
    JAVA_OPTS=$JAVA_OPTS\ -XX:+HeapDumpOnOutOfMemoryError
    # in local dump directory
    JAVA_OPTS=$JAVA_OPTS\ -XX:HeapDumpPath=${HEAP_DUMPS_PATH}
    echo "Heapdump will be written to ${HEAP_DUMPS_PATH}"
fi

if [ "${ENABLE_DEBUG}" = "suspend" ]; then
    DEBUG_ENABLED=true
    DEBUG_MODE="suspend=y"
else
    if [ "${ENABLE_DEBUG}" = "true" ]; then
      DEBUG_ENABLED=true
      DEBUG_MODE="suspend=n"
    else
      DEBUG_ENABLED=false
    fi
fi

if [ "${DEBUG_ENABLED}" = "true" ]; then
    # debug support
    DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,${DEBUG_MODE},address=*:7090"
    JAVA_OPTS=$JAVA_OPTS\ $DEBUG_OPTS
fi

if [ ! -z "${ENABLE_GCLOG}" -a "${ENABLE_GCLOG}" = "true" ]; then
    # garbage collector log (choose one)
    JAVA_OPTS=$JAVA_OPTS\ "-verbose:gc -XX:+PrintGCDetails"
fi

if [ ! "${SERVICE_CLASSPATH}" = "" ]; then
    JAVA_OPTS=$JAVA_OPTS\ -cp\ $SERVICE_CLASSPATH
fi

CMD_LINE="java ${JAVA_OPTS} -jar /app/app.jar"

echo Command Line:
echo "${CMD_LINE}"
echo

sh -c "$CMD_LINE"

exit $?
