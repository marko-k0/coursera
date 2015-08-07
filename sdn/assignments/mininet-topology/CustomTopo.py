'''
Coursera:
- Software Defined Networking (SDN) course
-- Programming Assignment 2
Professor: Nick Feamster
Teaching Assistant: Arpit Gupta, Muhammad Shahbaz
'''

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.node import CPULimitedHost
from mininet.link import TCLink
from mininet.util import irange,dumpNodeConnections
from mininet.log import setLogLevel

class CustomTopo(Topo):
    "Simple Data Center Topology"

    "linkopts - (1:core, 2:aggregation, 3: edge) parameters"
    "fanout - number of child switch per parent switch"
    def __init__(self, linkopts1, linkopts2, linkopts3, fanout=2, **opts):
        # Initialize topology and default options
        Topo.__init__(self, **opts)
        
        # Add your logic here ...
        self.fanout = fanout
        c1 = self.addSwitch('c1')
        for i in range(fanout):
            a = self.addSwitch('a%d' % (i + 1))
            self.addLink(a, c1, **linkopts1)
            for j in range(fanout):
                e = self.addSwitch('e%d' % (j + 1 + i * fanout))
                self.addLink(e, a, **linkopts2)
                for k in range(fanout):
                    h = self.addHost('h%d' % (k + 1 + i * fanout + j * fanout))
                    self.addLink(h, e, **linkopts3)
        
                    
topos = { 'custom': ( lambda: CustomTopo() ) }

def simpleTest(fanout=2):
    linkopts = {'bw': 10, 'delay': '5ms'}
    topo = CustomTopo(linkopts, linkopts, linkopts, fanout)
    net = Mininet(topo, link=TCLink)
    net.start()
    dumpNodeConnections(net.hosts)
    net.pingAll()
    net.stop()

if __name__ == '__main__':
    from mininet.log import setLogLevel
    setLogLevel('info')
    simpleTest()

