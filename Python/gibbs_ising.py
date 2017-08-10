import sys
import random
import math

write = sys.stdout.write

# Iba: p27
class gibbs_ising:
    def __init__(self):
        self.rand = random.random
        self.num_siml = 1000
        self.siml_loop = 0

        self.num_stat = 2
        self.stat = self.num_stat*[0]

        self.neighbor_x = [-1, 1]
        self.neighbor_y = [-1, 1]
        
    def init_state(self):
        for loop in range(self.size):
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
    def set_L(self, L):
        self.L = L
        self.size = self.L*self.L
        self.state = self.size * [0]
        
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
        idx_candidate_x = self.get_candidate()
        idx_candidate_y = self.get_candidate()
        idx = idx_candidate_x + self.L * idx_candidate_y
        
        r= self.get_r(idx_candidate_x, idx_candidate_y)
        accept = self.accept(r)
        if accept == 1:
            self.state[idx] = 1
        else:
            self.state[idx] = -1
            
        self.siml_loop += 1
        self.inc_stat()
        
    def get_candidate(self):
        return int(self.L * self.rand())
        
    def get_r(self, idx_candidate_x, idx_candidate_y):
        tmp = 0
        idx = idx_candidate_x + self.L * idx_candidate_y
        
        for loop_x in self.neighbor_x:
            nei_x = idx_candidate_x + loop_x
            if nei_x < 0 or nei_x >= self.L:
                continue
            
            for loop_y in self.neighbor_y:
                nei_y = idx_candidate_y + loop_y
                if nei_y < 0 or nei_y >= self.L:
                    continue

                tmp += self.state[nei_x + self.L * nei_y]

        return 1/(1+math.exp(-2 * self.theta * tmp))
        
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
    def print_state(self):
        print "--------------", self.siml_loop, "-----------------"
        for loop_y in range(self.L):
            for loop_x in range(self.L):
                if loop_x > 0:
                    write(' ')
                #write('\t')

                loop = loop_x + self.L * loop_y

                if self.state[loop] == 1:
                    write('+')
                elif self.state[loop] == -1:
                    write('-')
                else:
                    write('NA')
            write('\n')

        print "--------------------------------------"
        
        # print stat
        for loop in range(self.num_stat):
            write('\t')
            write(str(float(self.stat[loop])/float((1+self.siml_loop))))
        write('\n')                
        print "--------------------------------------"


def main():
    num_siml = 100
    hd = gibbs_ising()
    hd.set_theta(0.2)
    hd.set_L(8)
    
    hd.init_state()
    hd.print_state()
    for loop in range(num_siml):
        hd.simulation()
        hd.print_state()
    
if __name__ == '__main__':
    main()
    
