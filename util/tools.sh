#!/bin/bash

reldir=$(dirname $0)
project_home=${reldir}/..

command=${0##*/}

function get_version()
{
    local pom_file=${project_home}/pom.xml
    [ -f ${pom_file} ] || { echo "no ${pom_file} found" 1>&2; exit 1; }
    local first_version_line;
    first_version_line=$(cat ${pom_file} | grep "^ *<version>" | head -1) || { echo "failed to get <version> from ${pom_file}" 1>&2; exit 1; }
    [ "${first_version_line}" = "" ] && { echo "empty version from ${pom_file}" 1>&2; exit 1; }
    local version;
    version=${first_version_line#*>}
    version=${version%%\<*}
    [ "${version}" = "" ] && { echo "could not extract version from ${pom_file}" 1>&2; exit 1; }
    echo ${version}
}

function get_dependencies()
{
    (cd ${project_home}; mvn dependency:build-classpath -Dmdep.pathSeparator=" " -Dmdep.outputFile=./target/dependencies.txt 1>&2) || exit 1
    cat ${project_home}/target/dependencies.txt
}

force=false

while [ "$1" != "" ]; do
    if [ "$1" = "--force" ]; then
        force=true
    fi
    shift
done

case "${command}" in

  make-zip)

        (cd ${project_home}; mvn clean install) || exit 1

        version=$(get_version)

        mkdir -p ${project_home}/target/rld-${version}/lib
        mkdir ${project_home}/target/rld-${version}/bin

        cp ${project_home}/target/rld-${version}.jar ${project_home}/target/rld-${version}/lib && echo "rld-${version}.jar copied" || exit 1;

        cp ${project_home}/src/main/bash/rld ${project_home}/target/rld-${version}/bin && echo "rld copied" || exit 1;
        chmod a+rx ${project_home}/target/rld-${version}/bin/rld && echo "rld made executable" || exit 1;

        cp ${project_home}/src/main/bash/cca ${project_home}/target/rld-${version}/bin && echo "cca copied" || exit 1;
        chmod a+rx ${project_home}/target/rld-${version}/bin/cca && echo "cca made executable" || exit 1;

        cp ${project_home}/README ${project_home}/target/rld-${version} && echo "README copied" || exit 1;

        dependencies=$(get_dependencies) || exit 1
        for i in ${dependencies}; do
            [ -f ${i} ] || { echo "dependency ${i} does not exist" 1>&2; exit 1; }
            cp ${i} ${project_home}/target/rld-${version}/lib && echo $(basename ${i})" copied in ${project_home}/target/rld-${version}/lib" || exit 1;
        done

        (cd ${project_home}/target; zip -r ./rld-${version}.zip rld-${version}) && echo "${project_home}/target/rld-${version}.zip created" || exit 1
        ;;

  install-locally)

        [ "${RUNTIME_DIR}" = "" ] && { echo "'RUNTIME_DIR' environment variable not set. Set it and try again" 1>&2; exit 1; }
        [ -d ${RUNTIME_DIR} ] || { echo "'RUNTIME_DIR' ${RUNTIME_DIR} does not exist" 1>&2; exit 1; }

        version=$(get_version) || exit 1
        zip_file=${project_home}/target/rld-${version}.zip

        [ -f ${zip_file} ] || { echo "no release zip ${zip_file} found - run ${reldir}/make-zip" 1>&2; exit 1; }

        if [ -d ${RUNTIME_DIR}/rld-${version} ]; then
            if ${force}; then
                rm -r ${RUNTIME_DIR}/rld-${version} && \
                    echo "rld-${version} was installed already, but --force was used so it was removed" || \
                    { echo "failed to remove existing ${RUNTIME_DIR}/rld-${version}" 1>&2; exit 1; }
            else
                echo "rld-${version} already installed in ${RUNTIME_DIR} and --force flag was not used"
                exit 1;
            fi
        fi

        unzip ${zip_file} -d ${RUNTIME_DIR} || { echo "failed to unzip into ${RUNTIME_DIR}" 1>&2; exit 1; }

        if [ -h ${RUNTIME_DIR}/rld ]; then
            rm ${RUNTIME_DIR}/rld && echo "existing link ${RUNTIME_DIR}/rld was deleted" || \
                { echo "failed to delete the symbolic link ${RUNTIME_DIR}/rld"; exit 1; }
        fi

        (cd ${RUNTIME_DIR}; ln -s rld-${version} rld) && echo "installation successful"
        ;;

  *)
        echo "unknown command ${command}"
        exit 1
esac


