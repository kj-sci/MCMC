import sys
import random
import math

write = sys.stdout.write

# Iba: p19
class metlopolis_normaldist:
    def __init__(self):
        self.rand = random.random
        self.rand_norm = random.normalvariate
        # pdf = A * exp(-(x^2-2*beta*x*y+y^2)/2)
        self.beta = 0.8

        # step size
        self.sigma = 0.8
        
        self.dim = 2
        self.state = self.dim * [0]
        self.candidate = self.dim * [0]

        self.num_siml = 1000
        self.siml_loop = 0
        
    def init_state(self):
        for loop in range(self.dim):
                self.state[loop] = self.rand_norm(0, self.sigma)

    #########################################################
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #########################################################
    def set_beta(self, beta):
        self.beta = beta
        
    def set_sigma(self, sigma):
        self.sigma = sigma

    #########################################################
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #########################################################
    def simulation(self):
        for loop in range(self.dim):
            self.candidate[loop] = self.state[loop] + self.get_step()

        r= self.get_r()
        accept = self.accept(r)
        if accept == 1:
            for loop in range(self.dim):
                self.state[loop] = self.candidate[loop]
        self.siml_loop += 1
        
    def get_step(self):
        return self.rand_norm(0, self.sigma)
        
    def get_r(self):
        arg = self.state[0]*self.state[0] - 2*self.beta*self.state[0]*self.state[1] + self.state[1]*self.state[1]
        arg -= self.candidate[0]*self.candidate[0] - 2*self.beta*self.candidate[0]*self.candidate[1] + self.candidate[1]*self.candidate[1]
        return math.exp(float(arg)/float(2))
        
    def accept(self, r):
        this_r = self.rand()
        if this_r < r:
            return 1
        else:
            return 0

    #########################################################
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #                                                       #
    #########################################################
    def print_header(self):
        write('Loop')
        for loop in range(self.dim):
            write('\tx'+str(loop))

        write('\n')                


    def print_state(self):
        write(str(self.siml_loop))
        for loop in range(self.dim):
            #if loop > 0:
            #    write('\t')
            write('\t')

            write(str(self.state[loop]))
            
        write('\n')                


def main():
    num_siml = 100
    hd = metlopolis_normaldist()
    hd.print_header()
    hd.set_beta(0.8)
    hd.init_state()
    hd.print_state()
    for loop in range(num_siml):
        hd.simulation()
        hd.print_state()
    
if __name__ == '__main__':
    main()
    
