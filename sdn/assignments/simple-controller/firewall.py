'''
Coursera:
- Software Defined Networking (SDN) course
-- Programming Assignment: Layer-2 Firewall Application

Professor: Nick Feamster
Teaching Assistant: Arpit Gupta
'''

from pox.core import core
import pox.openflow.libopenflow_01 as of
from pox.lib.revent import *
from pox.lib.util import dpidToStr
from pox.lib.addresses import EthAddr
from collections import namedtuple
import os

log = core.getLogger()
policyFile = "%s/pox/pox/misc/firewall-policies.csv" % os.environ[ 'HOME' ]

''' Add your global variables here ... '''
f = open(policyFile)
rules_lst = f.readlines()[1:]
f.close()

class Firewall (EventMixin):

    def __init__ (self):
        global rules_lst
        self.listenTo(core.openflow)
        log.debug("Enabling Firewall Module")
        self.rules = []
        for rule in rules_lst:
            rule_parts = rule.split(",")
            self.rules.append(EthAddr(rule_parts[1]), EthAddr(rule_parts[2]))

    def _handle_ConnectionUp (self, event):
        ''' Add your logic here ... '''
        for src, dst in self.rules:
            msg = of.ofp_flow_mod()
            msg.match = of.ofp_match()
            msg.match.dl_src = src
            msg.match.dl_dst = dst
            msg.priority = 65535
            self.connection.send(msg)

        log.debug("Firewall rules installed on %s", dpidToStr(event.dpid))

def launch ():
    '''
    Starting the Firewall module
    '''
    core.registerNew(Firewall)
