import sys
import random

write = sys.stdout.write

class siml_sphere:
        def __init__(self):
            self.init(2, 1000)

        def init(self, dim, num_siml):
            self.cnt = 2*[0]            
            self.outdelim = '\t'

            self.dim = dim
            self.num_siml = num_siml
            self.sample = self.dim * [0]
            
        def siml(self):
            for loop in range(self.num_siml):
                self.do_siml()
        
        def do_siml(self):
            # get sample
            norm = 0
            for idx in range(self.dim):
                self.sample[idx] = 2*random.random()-1
                norm += self.sample[idx]*self.sample[idx]

            self.cnt[0] += 1
            if norm <= 1:
                self.cnt[1] += 1

        def result(self):
            area0 = 1
            for loop in range(self.dim):
                area0 *= 2
            area1 = float(self.cnt[1])/float(self.cnt[0]) * float(area0)
            
            write(str(self.dim))
            write(self.outdelim)
            write(str(self.num_siml))
            write(self.outdelim)
            write(str(self.cnt[0]))
            write(self.outdelim)
            write(str(self.cnt[1]))
            write(self.outdelim)
            write(str(area1))
            write("\n")

def main():
    num_siml = 100000

    hd = siml_sphere()
    print "dim\t#samples\tdenom\tnum\tvalue"
    for dim in range(1, 11):
        hd.init(dim, num_siml)
        hd.siml()
        hd.result()
    
if __name__ == '__main__':
    main()


    
