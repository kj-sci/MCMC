import sys
import random
import math

write = sys.stdout.write

# Iba: p10
class metlopolis_sample1:
    def __init__(self):
        self.rand = random.random
        self.num_x = 3
        self.state = self.num_x * [0]
        self.num_siml = 1000
        self.siml_loop = 0

        self.theta = 0.2
        self.num_stat = 2
        self.stat = self.num_stat*[0]
        
    def init(self):
        write("test")

    def init_state(self):
        for loop in range(self.num_x):
                r = 2*int(2*self.rand())-1
                self.state[loop] = r
        self.inc_stat()

    #########################################################
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #########################################################
    def set_theta(self, theta):
        self.theta = theta
        
    #########################################################
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #########################################################
    def simulation(self):
        idx_candidate = self.get_candidate()
        r= self.get_r(idx_candidate)
        accept = self.accept(r)
        if accept == 1:
            self.state[idx_candidate] = - self.state[idx_candidate]
        self.siml_loop += 1
        self.inc_stat()
        
    def get_candidate(self):
        return int(self.num_x * self.rand())
        
    def get_r(self, idx_candidate):
        arg = 0
        tmp = 0
        for loop in range(self.num_x):
            if loop == idx_candidate:
                continue
            tmp += self.state[loop]
        return math.exp(-2*self.theta*self.state[idx_candidate]*tmp)
        
    def accept(self, r):
        this_r = self.rand()
        if this_r < r:
            return 1
        else:
            return 0

    def inc_stat(self):
        self.stat[0] += self.state[0]
        self.stat[1] += self.state[0]*self.state[1]

    #########################################################
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #########################################################
    def print_header(self):
        write('Loop')
        for loop in range(self.num_x):
            write('\tx'+str(loop))

        # print stat
        write('\tavg(x0)\tavg(x0*x1)')
        write('\n')                


    def print_state(self):
        write(str(self.siml_loop))
        for loop in range(self.num_x):
            #if loop > 0:
            #    write('\t')
            write('\t')

            if self.state[loop] == 1:
                write('+')
            elif self.state[loop] == -1:
                write('-')
            else:
                write('NA')

        # print stat
        for loop in range(self.num_stat):
            write('\t')
            write(str(float(self.stat[loop])/float((1+self.siml_loop))))
            
        write('\n')                


def main():
    num_siml = 100
    hd = metlopolis_sample1()
    hd.print_header()
    hd.set_theta(0.8)
    hd.init_state()
    hd.print_state()
    for loop in range(num_siml):
        hd.simulation()
        hd.print_state()
    
if __name__ == '__main__':
    main()
    
