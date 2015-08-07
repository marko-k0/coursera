'''
Coursera:
- Software Defined Networking (SDN) course
-- Network Virtualization

Professor: Nick Feamster
Teaching Assistant: Arpit Gupta
'''

from pox.core import core
from collections import defaultdict

import pox.openflow.libopenflow_01 as of
import pox.openflow.discovery
import pox.openflow.spanning_tree
import pox.forwarding.l2_learning as l2l
from pox.lib.revent import *
from pox.lib.util import dpidToStr
from pox.lib.addresses import IPAddr, EthAddr
from collections import namedtuple
import os

log = core.getLogger()


class TopologySlice (EventMixin):

    def __init__(self):
        self.listenTo(core.openflow)
        log.debug("Enabling Slicing Module")


    """This event will be raised each time a switch will connect to the controller"""
    def _handle_ConnectionUp(self, event):

        # Use dpid to differentiate between switches (datapath-id)
        # Each switch has its own flow table. As we'll see in this
        # example we need to write different rules in different tables.
        dpid = dpidToStr(event.dpid)
        log.debug("Switch %s has come up.", dpid)

        """ Add your logic here """

        l2l.LearningSwitch(event.connection, False)

        s1_rules = { (IPAddr("10.0.0.1"), IPAddr("10.0.0.2")),
                     (IPAddr("10.0.0.1"), IPAddr("10.0.0.4")),
                     (IPAddr("10.0.0.2"), IPAddr("10.0.0.1")),
                     (IPAddr("10.0.0.2"), IPAddr("10.0.0.3"))}

        s4_rules = { (IPAddr("10.0.0.3"), IPAddr("10.0.0.2")),
                     (IPAddr("10.0.0.3"), IPAddr("10.0.0.4")),
                     (IPAddr("10.0.0.4"), IPAddr("10.0.0.1")),
                     (IPAddr("10.0.0.4"), IPAddr("10.0.0.3"))}

        msg = of.ofp_flow_mod()
        msg.priority = 65535
        msg.match = of.ofp_match()
        msg.match.dl_type = 0x800

        if dpid == "00-00-00-00-00-01":
            for srcIPAddr, dstIPAddr in s1_rules:
                msg.match.nw_src = srcIPAddr
                msg.match.nw_dst = dstIPAddr
                event.connection.send(msg)
        elif dpid == "00-00-00-00-00-04":
            for srcIPAddr, dstIPAddr in s4_rules:
                msg.match.nw_src = srcIPAddr
                msg.match.nw_dst = dstIPAddr
                event.connection.send(msg)


def launch():
    # Run spanning tree so that we can deal with topologies with loops
    pox.openflow.discovery.launch()
    pox.openflow.spanning_tree.launch()

    '''
    Starting the Topology Slicing module
    '''
    core.registerNew(TopologySlice)
