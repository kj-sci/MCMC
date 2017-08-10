import sys
import random
import math

write = sys.stdout.write

# Iba: p21
class gibbs_normaldist:
    def __init__(self):
        self.rand = random.random
        self.rand_norm = random.normalvariate
        # pdf = A * exp(-(x^2-2*beta*x*y+y^2)/2)
        self.beta = 0.8
        
        self.dim = 2
        self.state = self.dim * [0]
        self.stat = self.dim * [0]
        self.dim_loop = 0
        
        self.num_siml = 1000
        self.siml_loop = 0
        
    def init_state(self):
        for loop in range(self.dim):
                self.state[loop] = self.rand_norm(0, 1)
        self.inc_stat()

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
        self.siml_loop += 1

        idx = (self.dim_loop + 1) % self.dim
        mu = self.beta * self.state[idx]
        self.state[self.dim_loop] = self.rand_norm(mu, 1)
        self.dim_loop = (self.dim_loop + 1) % self.dim
        
        self.inc_stat()
        
    def inc_stat(self):
        for loop in range(self.dim):
            self.stat[loop] += self.state[loop]
   
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

        for loop in range(self.dim):
            write('\tavg(x'+str(loop)+')')

        write('\n')                


    def print_state(self):
        write(str(self.siml_loop))
        for loop in range(self.dim):
            #if loop > 0:
            #    write('\t')
            write('\t')
            write(str(self.state[loop]))

        for loop in range(self.dim):
            #if loop > 0:
            #    write('\t')
            write('\t')
            write(str(self.stat[loop]/float(self.siml_loop+1)))
            
        write('\n')                


def main():
    num_siml = 100
    hd = gibbs_normaldist()
    hd.print_header()
    hd.set_beta(0.8)
    hd.init_state()
    hd.print_state()
    for loop in range(num_siml):
        hd.simulation()
        hd.print_state()
    
if __name__ == '__main__':
    main()
    
