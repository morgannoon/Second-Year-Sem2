/**
 *  CarsPQ for CS1501 Project 3
 * @author    Morgan Noonan
 */

package cs1501_p3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;//it is in the textbook authors code for indexable Min PQ
import java.util.NoSuchElementException;

public class CarsPQ implements CarsPQ_Inter {
    private IndexMinPQ<Car> priceList;
    private IndexMinPQ<Car> mileageList;

    private DLB VINcheck;
    private DLB makeModel;

    public CarsPQ() {
        priceList = new IndexMinPQ<Car>('p');
        mileageList = new IndexMinPQ<Car>('m');
        VINcheck = new DLB();
        makeModel = new DLB();
    }

    public CarsPQ(String filename) {
        priceList = new IndexMinPQ<Car>('p');
        mileageList = new IndexMinPQ<Car>('m');
        VINcheck = new DLB();
        makeModel = new DLB();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.readLine(); // Read and discard the first line that shows the
            // breakdown

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String id = parts[0];
                String make = parts[1];
                String model = parts[2];
                int price = Integer.parseInt(parts[3]);
                int mileage = Integer.parseInt(parts[4]);
                String color = parts[5];
                if (!VINcheck.contains(id)) {
                    Car car = new Car(id, make, model, price, mileage, color);
                    String makeAndModel = make + model;
                    // ADD To PRICELIST AND MILEAGELIST
                    // iP = index in price pq
                    // iM = index in mileage pq
                    priceList.checkResize();
                    mileageList.checkResize();
                    int iP = priceList.insert(priceList.size() + 1, car);// FIGURE OUT WHERE AT INDEX i
                    int iM = mileageList.insert(mileageList.size() + 1, car);
                    VINcheck.add(car.getVIN(), iP, iM);
                    makeModel.add(makeAndModel, car);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing integer: " + e.getMessage());
        }

    }

    @Override
    public void add(Car c) throws IllegalStateException {
        if (c == null) {
            throw new IllegalStateException("illegal state exception");
        }
        if (VINcheck.contains(c.getVIN())) {
            return;
        }
        String makeAndModel = c.getMake() + c.getModel();
        // ADD To PRICELIST AND MILEAGELIST
        // iP = index in price pq
        // iM = index in mileage pq
        /*
         * need to figure out what iP and iM are goign to be set to
         *
         */

        priceList.checkResize();
        mileageList.checkResize();
        int iP = priceList.insert(priceList.size() + 1, c);// FIGURE OUT WHERE AT INDEX i
        int iM = mileageList.insert(mileageList.size() + 1, c);
        VINcheck.add(c.getVIN(), iP, iM);
        makeModel.add(makeAndModel, c);
    }

    @Override
    public Car get(String vin) throws NoSuchElementException {
        /* if !contains vin throw: */
        if (!VINcheck.contains(vin)) {
            throw new NoSuchElementException("no such element");
        }

        /* get to end of word in DLB return terminators attribute for price index */
        DLBNode curr = VINcheck.find(vin);
        int index = curr.getiP();
        return priceList.keyOf(index);
    }

    @Override
    public void updatePrice(String vin, int newPrice) throws NoSuchElementException {
        /* if vin doesn't exists throw: */
        if (!VINcheck.contains(vin)) {
            throw new NoSuchElementException("no such element");
        }
        /* if it does, change its price, reheapify (sink/swim) */
        DLBNode curr = VINcheck.find(vin);
        int iP = curr.getiP();
        int iM = curr.getiM();
        Car temp = new Car(vin, priceList.keyOf(iP).getMake(), priceList.keyOf(iP).getModel(),
                newPrice, priceList.keyOf(iP).getMileage(), priceList.keyOf(iP).getColor());

        // reheapify mileageList and update priceList
        priceList.changeKey(iP, temp);
        mileageList.keyOf(iM).setMileage(newPrice);
        // reheapify priceList
    }

    @Override
    public void updateMileage(String vin, int newMileage) throws NoSuchElementException {
        /* if vin doesn't exists throw: */
        if (!VINcheck.contains(vin)) {
            throw new NoSuchElementException("no such element");
        }
        /* if it does, change its mileage, reheapify (sink/swim) */
        DLBNode curr = VINcheck.find(vin);
        int iP = curr.getiP();
        int iM = curr.getiM();
        Car temp = new Car(vin, mileageList.keyOf(iM).getMake(), mileageList.keyOf(iM).getModel(),
                mileageList.keyOf(iM).getPrice(), newMileage, mileageList.keyOf(iM).getColor());

        // reheapify mileageList and update priceList
        mileageList.changeKey(iM, temp);
        priceList.keyOf(iP).setMileage(newMileage);
    }

    @Override
    public void updateColor(String vin, String newColor) throws NoSuchElementException {
        /* if vin doesn't exists throw: */
        if (!VINcheck.contains(vin)) {
            throw new NoSuchElementException("no such element");
        }
        /* if it does, change its color */
        DLBNode curr = VINcheck.find(vin);
        int iP = curr.getiP();
        int iM = curr.getiM();
        priceList.keyOf(iP).setColor(newColor);
        mileageList.keyOf(iM).setColor(newColor);

    }

    @Override
    public void remove(String vin) throws NoSuchElementException {
        /* if vin doesn't exist throw: */
        if (!VINcheck.contains(vin)) {
            throw new NoSuchElementException("no such element");
        }
        /*
         * if it does remove it from VIN DLB, makeModel DLB, remove it from priceList
         * and mileageList -->reheapify
         */
        DLBNode curr = VINcheck.find(vin);
        int iP = curr.getiP();
        int iM = curr.getiM();
        String makeAndModel = priceList.keyOf(iP).getMake() + priceList.keyOf(iM).getModel();

        VINcheck.remove(vin);
        makeModel.remove(makeAndModel, vin);
        priceList.delete(iP);
        mileageList.delete(iM);
    }

    @Override
    public Car getLowPrice() {
        /* return pqPriceList min otherwise return null */
        if (priceList == null) {
            return null;
        }
        return priceList.minKey();
    }

    @Override
    public Car getLowPrice(String make, String model) {
        /*
         * can't find a car with same make and model return null
         * /*
         * if there's a match return the first hit, that = min
         */
        String makeAndModel = make + model;
        if (!makeModel.contains(makeAndModel)) {
            return null;
        }
        // check all that match it but grab the one with the minimum index for price
        DLBNode temp = makeModel.find(makeAndModel);
        Car minCar = temp.getPrice().minKey();
        return minCar;

    }

    @Override
    public Car getLowMileage() {
        /*
         * return null if there isn't one
         * otherwise return mileagePriceList min
         */
        if (mileageList == null) {
            return null;
        }
        return mileageList.minKey();
    }

    @Override
    public Car getLowMileage(String make, String model) {
        /* can't find a car with same make and model return null */
        /*
         * if there's a match return the first hit, that = min
         */
        String makeAndModel = make + model;
        if (!makeModel.contains(makeAndModel)) {
            return null;
        }
        // check all that match it but grab the one with the minimum index for price
        DLBNode temp = makeModel.find(makeAndModel);
        Car minCar = temp.getMileage().minKey();
        return minCar;

    }

    /******************************************************************************** */
    private class DLB {
        private DLBNode root;

        private DLB() {
        }

        // remove for VIN
        private void remove(String vin) {
            if (contains(vin)) {
                DLBNode curr = root;
                DLBNode prev = curr;

                for (int k = 0; k < vin.length(); k++) {
                    curr = checkR(curr, vin.charAt(k));
                    if (prev.getDown() != curr && prev != curr) {
                        prev = prev.getDown();
                        while (prev.getRight() != null && prev.getRight() != curr) {
                            prev = prev.getRight();
                        }
                    }
                    curr = curr.getDown();
                }
                if (prev.getRight() == curr) {
                    prev.setRight(curr.getRight());
                } else {
                    prev.setDown(curr.getRight());
                }
            }
        }

        // remove for makeModel
        private void remove(String makeModel, String vin) {
            if (contains(vin)) {
                DLBNode curr = find(makeModel);
                curr.getPrice().delete(curr.getPrice().getIndex(vin));
                curr.getMileage().delete(curr.getMileage().getIndex(vin));
            }
        }

        // return DLBNode terminator of given VIN
        private DLBNode find(String key) {
            DLBNode curr = null;
            if (contains(key)) {
                curr = root;
                for (int k = 0; k < key.length(); k++) {
                    curr = checkR(curr, key.charAt(k));
                    curr = curr.getDown();
                }
            }
            return curr;
        }

        // for VIN index in price (iP), index in mileage (iM)
        private void add(String key, int iP, int iM) {
            if (key == null || key.isEmpty()) {
                return;
            }
            if (root == null) { // DLB is empty
                root = new DLBNode(key.charAt(0));
                DLBNode curr = root;
                for (int k = 1; k < key.length(); k++) {
                    DLBNode addChar = new DLBNode(key.charAt(k));
                    curr.setDown(addChar);
                    curr = curr.getDown();
                }
                DLBNode addChar = new DLBNode('!', iP, iM);
                curr.setDown(addChar);
            } else {// DLB not empty
                DLBNode index = root;
                DLBNode prev = null;
                for (int k = 0; k < key.length(); k++) {
                    index = nextR(index, key.charAt(k), prev); // go through LL getRight, if not there set right
                    if (index.getDown() == null) {// if down doesn't exist add down new char
                        DLBNode addChar;
                        if (k == key.length() - 1) {
                            addChar = new DLBNode('!', iP, iM);
                        } else {
                            addChar = new DLBNode(key.charAt(k));
                        }
                        index.setDown(addChar);
                    }
                    prev = index;
                    index = index.getDown(); // move down
                }
            }
        }

        // for makeModel in price (iP), index in mileage (iM)
        private void add(String key, Car c) {
            if (key == null || key.isEmpty()) {
                return;
            }
            if (root == null) { // DLB is empty
                root = new DLBNode(key.charAt(0));
                DLBNode curr = root;
                for (int k = 1; k < key.length(); k++) {
                    DLBNode addChar = new DLBNode(key.charAt(k));
                    curr.setDown(addChar);
                    curr = curr.getDown();
                }
                DLBNode addChar = new DLBNode('!', c);
                curr.setDown(addChar);
            } else {// DLB not empty
                DLBNode index = root;
                DLBNode prev = null;
                for (int k = 0; k < key.length(); k++) {
                    index = nextR(index, key.charAt(k), prev); // go through LL getRight, if not there set right
                    if (index.getDown() == null) {// if down doesn't exist add down new char
                        DLBNode addChar;
                        if (k == key.length() - 1) {
                            addChar = new DLBNode('!', c);

                        } else {
                            addChar = new DLBNode(key.charAt(k));
                        }
                        index.setDown(addChar);
                    }
                    prev = index;
                    index = index.getDown(); // move down
                }
            }
        }

        private DLBNode nextR(DLBNode curr, char k, DLBNode prev) {
            if (prev != null && prev.getDown().getLet() > k) {
                if (curr == root) {
                    DLBNode temp = new DLBNode(k);
                    temp.setRight(curr);
                    root = temp;
                    return temp;
                }
                DLBNode temp = new DLBNode(k);
                temp.setRight(prev.getDown());
                prev.setDown(temp);
                return temp;
            }
            if (curr.getLet() == k) {
                return curr;
            }
            if (curr.getRight() != null && curr.getRight().getLet() > k) {
                DLBNode temp = new DLBNode(k);
                temp.setRight(curr.getRight());
                curr.setRight(temp);
                return temp;
            }
            if (curr.getRight() == null) {
                DLBNode temp = new DLBNode(k);
                curr.setRight(temp);
                return temp;
            } else {
                return nextR(curr.getRight(), k, prev);
            }
        }

        private boolean contains(String key) {
            if (root == null || key == null || key.isEmpty()) {
                return false;
            }
            DLBNode curr = root;
            for (int k = 0; k < key.length(); k++) {
                curr = checkR(curr, key.charAt(k));
                if (curr == null || curr.getDown() == null) {
                    return false;
                }
                curr = curr.getDown();
            }
            if (curr != null && curr.getDown() != null && curr.getDown().getLet() != '!') {
                return false;
            }
            return true;
        }

        private DLBNode checkR(DLBNode curr, char k) {
            if (curr == null) {
                return null;
            }
            if (curr.getLet() == k) {
                return curr;
            }
            if (curr.getRight() == null) {
                return null;
            }
            return checkR(curr.getRight(), k);
        }
    }

    /******************************************************************************** */
    private class DLBNode {

        private char let;
        private IndexMinPQ<Car> price;
        private IndexMinPQ<Car> mileage;
        private int iM;
        private int iP;

        private DLBNode right;
        private DLBNode down;

        private DLBNode(char let) {
            this.let = let;
            this.price = null;
            this.mileage = null;
            this.right = null;
            this.down = null;
        }

        // VIN dlb terminator
        private DLBNode(char let, int iP, int iM) {
            this.let = let;
            this.iP = iP - 1;
            this.iM = iM - 1;
            this.right = null;
            this.down = null;
        }

        // makeModel DLB terminator
        private DLBNode(char let, Car c) {
            this.let = let;
            this.price = new IndexMinPQ<Car>('p');
            this.price.insert(price.size() + 1, c);
            this.mileage = new IndexMinPQ<Car>('m');
            this.mileage.insert(mileage.size() + 1, c);
            this.right = null;
            this.down = null;
        }

        private int getiP() {
            return this.iP + 1;
        }

        private void setiP(int p) {
            this.iP = p;
        }

        private int getiM() {
            return this.iM + 1;
        }

        private void setiM(int m) {
            this.iM = m;
        }

        private IndexMinPQ<Car> getMileage() {
            return this.mileage;
        }

        private IndexMinPQ<Car> getPrice() {
            return this.price;
        }

        private char getLet() {
            return let;
        }

        private DLBNode getRight() {
            return right;
        }

        private DLBNode getDown() {
            return down;
        }

        private void setRight(DLBNode r) {
            right = r;
        }

        private void setDown(DLBNode d) {
            down = d;
        }

    }

    /******************************************************************************** */

    private class IndexMinPQ<Car> implements Iterable<Integer> {
        private int maxN; // maximum number of elements on PQ
        private int n; // number of elements on PQ
        private int[] pq; // binary heap using 1-based indexing
        private int[] qp; // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
        private Car[] keys; // keys[i] = priority of i
        private char type;

        /**
         * Initializes an empty indexed priority queue with indices between {@code 0}
         * and {@code maxN - 1}.
         * 
         * @param maxN the keys on this priority queue are index from {@code 0}
         *             {@code maxN - 1}
         * @throws IllegalArgumentException if {@code maxN < 0}
         */
        private IndexMinPQ(char t) {
            n = 0;
            type = t;
            maxN = 100;
            keys = (Car[]) new Object[maxN]; // make this of length maxN??
            pq = new int[maxN];
            qp = new int[maxN]; // make this of length maxN??
            for (int i = 1; i < maxN; i++)
                qp[i] = -1;
        }

        public int getIndex(String vin) {
            for (int i = 1; i < n; i++) {
                Car temp = keys[pq[i]];
                if (vin.equals(((cs1501_p3.Car) temp).getVIN()))
                    return i;
            }
            return -1;// not in pq
        }

        private IndexMinPQ(int size, char t) {
            n = 0;
            type = t;
            maxN = size;
            keys = (Car[]) new Object[size]; // make this of length maxN??
            pq = new int[size];
            qp = new int[size]; // make this of length maxN??
            for (int i = 1; i < size; i++)
                qp[i] = -1;
        }

        /*
         * checks for need to resize, if needs to will double
         */
        private void checkResize() {
            if (n == maxN) {
                int newCapacity = 2 * maxN;
                Car[] newKeys = (Car[]) new Object[newCapacity]; // Create a new array of Objects and cast it to Car[]

                // Copy elements from old array to new array
                System.arraycopy(keys, 0, newKeys, 0, maxN);

                // Update references
                keys = newKeys;
                maxN = newCapacity;
            }
        }

        /**
         * Returns true if this priority queue is empty.
         *
         * @return {@code true} if this priority queue is empty;
         *         {@code false} otherwise
         */
        private boolean isEmpty() {
            return n == 0;
        }

        /**
         * Is {@code i} an index on this priority queue?
         *
         * @param i an index
         * @return {@code true} if {@code i} is an index on this priority queue;
         *         {@code false} otherwise
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         */
        private boolean contains(int i) {
            validateIndex(i);
            return qp[i] != -1;
        }

        /**
         * Returns the number of keys on this priority queue.
         *
         * @return the number of keys on this priority queue
         */
        private int size() {
            return n;
        }

        /**
         * Associates key with index {@code i}.
         *
         * @param i   an index
         * @param key the key to associate with index {@code i}
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws IllegalArgumentException if there already is an item associated
         *                                  with index {@code i}
         */
        private int insert(int i, Car key) {
            validateIndex(i);
            if (contains(i))
                throw new IllegalArgumentException("index is already in the priority queue");
            n++;
            qp[i] = n;
            pq[n] = i;
            keys[i] = key;
            int index = swimIndex(n);
            return index;
        }

        private void insertCopy(int i, Car key) {
            validateIndex(i);
            if (contains(i))
                throw new IllegalArgumentException("index is already in the priority queue");
            n++;
            qp[i] = n;
            pq[n] = i;
            keys[i] = key;
            swim(n);
        }

        /**
         * Returns a minimum key.
         *
         * @return a minimum key
         * @throws NoSuchElementException if this priority queue is empty
         */
        private Car minKey() {
            if (n == 0)
                throw new NoSuchElementException("Priority queue underflow");
            return keys[pq[1]];
        }

        /**
         * Removes a minimum key and returns its associated index.
         * 
         * @return an index associated with a minimum key
         * @throws NoSuchElementException if this priority queue is empty
         */
        private int delMin() {
            if (n == 0)
                throw new NoSuchElementException("Priority queue underflow");
            int min = pq[1];
            exch(1, n--);
            sink(1);
            assert min == pq[n];
            qp[min] = -1; // delete
            keys[min] = null; // to help with garbage collection
            pq[n] = -1; // not needed
            return min;
        }

        /**
         * Returns the key associated with index {@code i}.
         *
         * @param i the index of the key to return
         * @return the key associated with index {@code i}
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws NoSuchElementException   no key is associated with index {@code i}
         */
        private Car keyOf(int i) {
            validateIndex(i);
            if (!contains(i))
                throw new NoSuchElementException("index is not in the priority queue");
            else
                return keys[i];
        }

        /**
         * Change the key associated with index {@code i} to the specified value.
         *
         * @param i   the index of the key to change
         * @param key change the key associated with index {@code i} to this key
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws NoSuchElementException   no key is associated with index {@code i}
         */
        private void changeKey(int i, Car key) {
            validateIndex(i);
            if (!contains(i))
                throw new NoSuchElementException("index is not in the priority queue");
            keys[i] = key;
            swim(qp[i]);
            sink(qp[i]);
        }

        // Integer.compare(this.car.getPrice(), other.car.getPrice())

        /**
         * Remove the key associated with index {@code i}.
         *
         * @param i the index of the key to remove
         * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
         * @throws NoSuchElementException   no key is associated with index {@code i}
         */
        private void delete(int i) {
            validateIndex(i);
            if (!contains(i))
                throw new NoSuchElementException("index is not in the priority queue");
            int index = qp[i];
            exch(index, n--);
            swim(index);
            sink(index);
            keys[i] = null;
            qp[i] = -1;

        }

        // throw an IllegalArgumentException if i is an invalid index
        private void validateIndex(int i) {
            if (i < 0)
                throw new IllegalArgumentException("index is negative: " + i);
            if (i >= maxN)
                throw new IllegalArgumentException("index >= capacity: " + i);
        }

        /***************************************************************************
         * General helper functions.
         ***************************************************************************/
        private boolean greater(int i, int j) {
            if (type == 'p') {
                if (((cs1501_p3.Car) keys[pq[i]]).getPrice() > ((cs1501_p3.Car) keys[pq[j]]).getPrice())
                    return true;
                else
                    return false;
            } else if (((cs1501_p3.Car) keys[pq[i]]).getMileage() > ((cs1501_p3.Car) keys[pq[j]]).getMileage())
                return true;
            else
                return false;

        }

        private void exch(int i, int j) {
            int swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
            qp[pq[i]] = i;
            qp[pq[j]] = j;
        }

        /***************************************************************************
         * Heap helper functions.
         ***************************************************************************/
        private void swim(int k) {
            while (k > 1 && greater(k / 2, k)) {
                exch(k, k / 2);
                k = k / 2;
            }
        }

        private void sink(int k) {
            while (2 * k <= n) {
                int j = 2 * k;
                if (j < n && greater(j, j + 1))
                    j++;
                if (!greater(k, j))
                    break;
                exch(k, j);
                k = j;
            }
        }

        private int swimIndex(int k) {
            while (k > 1 && greater(k / 2, k)) {
                exch(k, k / 2);
                k = k / 2;
            }
            return k;
        }

        /***************************************************************************
         * Iterators.
         ***************************************************************************/

        /**
         * Returns an iterator that iterates over the keys on the
         * priority queue in ascending order.
         * The iterator doesn't implement {@code remove()} since it's optional.
         *
         * @return an iterator that iterates over the keys in ascending order
         */
        public Iterator<Integer> iterator() {
            return new HeapIterator();
        }

        private class HeapIterator implements Iterator<Integer> {
            // create a new pq
            private IndexMinPQ<Car> copy;

            // add all elements to copy of heap
            // takes linear time since already in heap order so no keys move
            public HeapIterator() {
                copy = new IndexMinPQ<Car>((pq.length - 1), 'p');
                for (int i = 1; i <= n; i++)
                    copy.insertCopy(pq[i], keys[pq[i]]);
            }

            public boolean hasNext() {
                return !copy.isEmpty();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Integer next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return copy.delMin();
            }
        }

    }
    /******************************************************************************** */

}
