#!/usr/bin/env sh

##############################################################################
# void checkVar(string varName)
##############################################################################
#
# Checks if an environment variable is set and not empty.
#
# Example:
#
#   checkVar "MY_VAR"
#
# Return codes: 0 - everything fine
#               1 - missing or empty
#
# Notice: this function may echo messages to stderr.
#
##############################################################################
checkVar() {
    local VAR_NAME="$1"
    eval "local VAR_VALUE=\$$VAR_NAME"

    if [ -z "${VAR_VALUE}" ] ; then
        echo "Required environment variable '${VAR_NAME}' is missing or empty." >&2
        return 1
    fi
}

##############################################################################
# void checkAllVars(string varName0[, string varName1 ... [, string varNameN]])
##############################################################################
#
# Checks if a list of environment variables are set and not empty.
#
# Example:
#
#   checkAllVars "MY_VAR0" "MY_VAR1"  "MY_VAR2"
#
# Return codes: 0 - everything fine
#               <number of missing or empty variables> - if at least 1 variable is missing or empty
#
# Notice: this function may echo messages to stderr.
#
##############################################################################
checkAllVars() {
    local ERROR_COUNT=0
    for VAR_NAME in $* ; do
        checkVar "$VAR_NAME"
        local RESULT_CODE=$?
        ERROR_COUNT=$((ERROR_COUNT+RESULT_CODE))
    done
    return $ERROR_COUNT
}

##############################################################################
# String serverDirProperty(string propertyKey)
##############################################################################
#
# Checks if a property in serverdir.properties exists and prints it trimmed.
#
# Example:
#
#   serverDirProperty "YOUR_PROPERTY"
#
# Return codes: 0 - everything fine
#               1 - property not found
#
# Notice: this function may echo messages to stderr.
#
##############################################################################
serverDirProperty() {
    local PROPERTY_VALUE
    PROPERTY_VALUE=$(grep "${1}" "/intershop/serverdir.properties" | cut -d'=' -f2)

    if [ -z "${PROPERTY_VALUE}" ] ; then
        echo "Property '${1}' is missing or empty." >&2
        return 1
    fi
    # trim leading/trailing whitespace
    PROPERTY_VALUE="$(echo "${PROPERTY_VALUE}" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')"

    echo "${PROPERTY_VALUE}"
}
