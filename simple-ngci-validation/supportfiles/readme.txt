The validation poc leverages requires a pxeboot image containing the required lldp tools.

See CustomDebianLiveCD.docx for info on creating such an image using a chrooted environment

The following lists some of the additional packages installed in the environment
apt-get install --no-install-recommends pciutils lldpad
apt-get install --no-install-recommends tcpdump openssh-client
apt-get install --no-install-recommends     network-manager net-tools
apt-get install net-tools
apt-get install iputils-arping iputils-ping
apt-get install lldpd
apt-get install traceroute
apt-get install dmidecode
apt-get install dialog apt-utils

The directory etc contains some configuration files for the server hosting this application

