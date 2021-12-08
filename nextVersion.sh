#/bin/bash
#
# This script takes currrent version from gradle.properties file and generates new version
# Version is assumed to be in the format YYYYMM.NR-SNAPSHOT, where NR = current release number
# If current version year and month are current, then only release number is incremented. Otherwise
# current year and month is used in new version and release number is reset to 1
#

# Get current date's year and month
current_year=$(date +'%Y')
current_month=$(date +'%m')

# Get year, month and release number from gradle.properties file
version_regex="version=([0-9]{4})([0-9]{2})\.([0-9]{1,10})-SNAPSHOT"
version_year=""
version_month=""
version_number=""
while read -r line; do
    if [[ $line =~ $version_regex ]]
    then
      version_year="${BASH_REMATCH[1]}"
      version_month="${BASH_REMATCH[2]}"
      version_number="${BASH_REMATCH[3]}"
    fi
done < "gradle.properties"

[[ -z "$version_year" ]] && { echo "Error: Can not get version from gradle.properties"; exit 1; }

if [[ ( $current_year == $version_year && $current_month == $version_month ) ]]
then
  # If year and month are the same, just increment release number
  new_year=$version_year 
  new_month=$version_month
  new_version=$(($version_number + 1 ))
else
  # Otherwise, set new version to current year and month and reset release number to 1
  new_year=$current_year
  new_month=$current_month
  new_version=1
fi

echo "next_version=$new_year$new_month.$new_version-SNAPSHOT"
