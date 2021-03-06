import argparse
import json
import os
import requests
import sys

#################### Start Functions ####################


#################### End Functions ####################

print('')
print('Start...')
print('')

#jobid = 5
#payload = {'jobid': jobid, 'progress': 0, 'message': 'Started'}
#resp = requests.post('http://10.234.122.102:8400/bootstrap/status', params=payload)
#print(resp)

#resp = requests.post('http://localhost:8300/bootstrap/jobdetails/1')

#input_json = '{"idrac_ip":"10.234.122.15","node_ip":"172.31.128.14","node_disk_name":"ATA","esxi_version":"6.5","esxi_hostname":"rvx2-host06","esxi_ip":"10.234.122.76","esxi_netmask":"255.255.255.192","esxi_gateway":"10.234.122.65","esxi_dns":"10.136.112.220","esxi_dns_search":"lab.vce.com","esxi_nic":"vmnic1","esxi_vlanid":"126","esxi_root_password":"Vc3m0l@b","esxi_accepteula":"true"}'
# Set Arguments
parser = argparse.ArgumentParser()
#parser.add_argument('--input_json', required=True)
parser.add_argument('--jobid', required=True)
args = parser.parse_args()
jobid = args.jobid
resp = requests.get('http://localhost:8400/bootstrap/jobdetails/' + jobid)

#resp.status_code = 200
#resp.text = {"jobId":1,"jobName":"install_esxi_job.sh","progress":0,"messages":["Starting install_esxi_job.sh 1"],"consoleMessages":["ThatsAll"],"parameters":{"esxi_root_password":["hello"],"node_disk_name":["ATA"],"esxi_netmask":["255.255.255.255"],"esxi_dns":["10.1.30.134"],"esxi_nic":["vnic0"],"idrac_ip":["10.1.1.1"],"esxi_hostname":["a.host.name"],"job":["install_esxi_job.sh"],"esxi_dns_search":["abc.local.com"],"esxi_version":["6.5.0"],"esxi_vlanid":["25"],"esxi_gateway":["10.100.3.45"]}}
#input_json = parameters

 
progress_payload = {'jobid': jobid, 'progress': 0, 'message': 'Started'}
progress_resp = requests.post('http://10.234.122.102:8400/bootstrap/status', params=progress_payload)

#input_json = args.input_json
input_json = {}
for key in resp.json()['parameters']:
    input_json[key] = resp.json()['parameters'][key][0]

# Print Input
print('IDRAC IP: %s' % input_json['idrac_ip'])
print('Node IP: %s' % input_json['node_ip'])
print('Node Boot Disk: %s' % input_json['node_disk_name'])
print('ESXi Version: %s' % input_json['esxi_version'])
print('ESXi Hostname: %s' % input_json['esxi_hostname'])
print('ESXi IP: %s' % input_json['esxi_ip'])
print('ESXi Netmask: %s' % input_json['esxi_netmask'])
print('ESXi Gateway: %s' % input_json['esxi_gateway'])
print('ESXi DNS: %s' % input_json['esxi_dns'])
print('ESXi DNS Search: %s' % input_json['esxi_dns_search'])
print('ESXi NIC: %s' % input_json['esxi_nic'])
print('ESXi VLAN: %s' % input_json['esxi_vlanid'])
print('ESXi Root Password: **********')
print('ESXi EULA Accepted: %s' % input_json['esxi_accepteula'])
print('')

if input_json['esxi_accepteula'] != 'on':
    raise Exception('Unaccept ESXi EULA')

# Set Hex File Names
hex_ip = input_json['node_ip']
hex_ip_list = hex_ip.split('.')
hex_file_name = format(int(hex_ip_list[0]), '02X') + format(int(hex_ip_list[1]), '02X') + format(int(hex_ip_list[2]), '02X') + format(int(hex_ip_list[3]), '02X')
ks_file_name = hex_file_name + '.cfg'
print('HEX File: %s' % hex_file_name)
print('ks.cfg File: %s' % ks_file_name)

# Set PXI Boot File Content
my_file_contents = 'DEFAULT esxi\n'
my_file_contents = my_file_contents + 'TIMEOUT 20\n'
my_file_contents = my_file_contents + 'PROMPT 0\n'
my_file_contents = my_file_contents + 'LABEL pxeboot\n'
my_file_contents = my_file_contents + '    KERNEL deblive/vmlinuz1\n'
my_file_contents = my_file_contents + '    APPEND initrd=deblive/initrd1 boot=live fetch=tftp://172.31.128.1/deblive/filesystem.squashfs --\n'
my_file_contents = my_file_contents + 'ONERROR LOCALBOOT 0\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + 'LABEL esxi\n'
if input_json['esxi_version'] == '6.5' or input_json['esxi_version'] == '6.5.0':
    my_file_contents = my_file_contents + '    KERNEL images/ESXi-6.5.0/mboot.c32\n'
    my_file_contents = my_file_contents + '    APPEND -c images/ESXi-6.5.0/boot.cfg ks=http://172.31.128.1/esxi_ksFiles/' + ks_file_name + '\n'
elif input_json['esxi_version'] == '6.0' or input_json['esxi_version'] == '6.0.0':
    my_file_contents = my_file_contents + '    KERNEL images/ESXi-6.0.0/mboot.c32\n'
    my_file_contents = my_file_contents + '    APPEND -c images/ESXi-6.0.0/boot.cfg ks=http://172.31.128.1/esxi_ksFiles/' + ks_file_name + '\n'
else:
    raise Exception('Invalid ESXi version')
my_file_contents = my_file_contents + '    IPAPPEND 2\n'
my_file_contents = my_file_contents + 'ONERROR LOCALBOOT 0\n'
my_file_contents = my_file_contents + '\n'

with open('/var/lib/tftpboot/pxelinux.cfg/' + hex_file_name, 'w') as my_file_handle:
    my_file_handle.write(my_file_contents)
# TO DO
# Add Call to UI API to show step completed
progress_payload = {'jobid': jobid, 'progress': 30, 'message': 'Files Created'}
progress_resp = requests.post('http://10.234.122.102:8400/bootstrap/status', params=progress_payload)

# Set Kiickstart File Content
my_file_contents = '##### Stage 01 - Pre installation:	\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Accept the VMware End User License Agreement\n'
my_file_contents = my_file_contents + '    vmaccepteula\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Set the root password for the DCUI and Tech Support Mode\n'
my_file_contents = my_file_contents + '    rootpw ' + input_json['esxi_root_password'] + '\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### The install media (priority: local / remote / USB)\n'
my_file_contents = my_file_contents + '    install --firstdisk="' + input_json['node_disk_name'] + '" --overwritevmfs --novmfsondisk\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Set the network to DHCP on the first network adapter\n'
my_file_contents = my_file_contents + '    network --bootproto=static --device=' + input_json['esxi_nic'] + ' --ip=' + input_json['esxi_ip'] + ' --netmask=' + input_json['esxi_netmask'] + ' --gateway=' + input_json['esxi_gateway'] + ' --nameserver=' + input_json['esxi_dns'] + ' --hostname=' + input_json['esxi_hostname'] + ' --addvmportgroup=0 --vlanid=' + input_json['esxi_vlanid'] + '\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Reboot ESXi Host\n'
my_file_contents = my_file_contents + '    reboot --noeject\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '##### Stage 02 - Post installation:\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Open busybox and launch commands\n'
my_file_contents = my_file_contents + '    %firstboot --interpreter=busybox\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Set Search Domain\n'
my_file_contents = my_file_contents + '    esxcli network ip dns search add --domain=' + input_json['esxi_dns_search'] + '\n'
my_file_contents = my_file_contents + '\n'
my_file_contents = my_file_contents + '    ### Reboot\n'
my_file_contents = my_file_contents + '    esxcli system shutdown reboot -d 15 -r "rebooting after ESXi host configuration"\n'
my_file_contents = my_file_contents + '\n'

with open('/var/www/lighttpd/esxi_ksFiles/' + ks_file_name, 'w') as my_file_handle:
    my_file_handle.write(my_file_contents) 
# TO DO
# Add Call to UI API to show step completed

# PXE Boot Node
progress_payload = {'jobid': jobid, 'progress': 75, 'message': 'PXE Booting'}
progress_resp = requests.post('http://10.234.122.102:8400/bootstrap/status', params=progress_payload)

# os.system('ansible-playbook set_boot_then_reboot.yml')

# TO DO
# Add Call to UI API to show step completed

# TO DO
# Add PING to ESXi to verify install
# Add Call to UI API to show step completed

print('')
print('...End')
print('')
