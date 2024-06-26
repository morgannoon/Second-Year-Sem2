[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-7f7980b617ed060a017424585567c406b6ee15c891e84e1186181d67ecf80aa0.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=14153920)
# CS 1501 LZW Lab

## Goal:
To understand the inner-workings and implementation of the LZW compression
algorithm, and to gain a better understanding of the performance it offers.


## High-level description:
As we discussed in lecture, LZW is a compression algorithm that was created in
1984 by Abraham Lempel, Jacob Ziv, and Terry Welch. In its most basic form, it
will output a compressed file as a series of fixed-length codewords. This is the
approach implemented in the LZW code provided by the authors of the textbook.
As we discussed in class, *variable-width* codewords can be used to increase
the size of codewords output as the dictionary fills up.

For this project, you will be modifying the LZW source code provided by the
authors of the text book to use variable-width codewords.


## Specifications:
1. Before making the required changes to `LZW.java`, you will need to read
	through the code, and run example compressions/expansions to understand
	how it is currently working. Note that `LZW.java` requires the following
	library files (also developed by the textbook authors): `BinaryStdIn.java`,
	`BinaryStdOut.java`, `TST.java`, `Queue.java`, `StdIn.java`, and `StdOut.java`.
	These files have already been added to your repository.

1. With a firm understanding of the provided code in hand, you can proceed to
	make the following changes to `LZW.java`:

	* Make it so that the algorithm will vary the size of the output/input codewords
		from 9 to 16 bits.

	* The codeword size should be increased when all of the codewords of a previous
		size have been used.


## Submission Guidelines:
* **DO NOT** add the `./app/build/` diectory to your repository.
    * Leave the `./app/build.gradle` file there, however

* Be sure to remember to push the latest copy of your code back to your GitHub
    repository before submitting. To submit, log into GradeScope from Canvas and
    have GradeScope pull your repository from GitHub.


## Additional Notes/Hints:
* In the author's code the bits per codeword (W) and number of codewords
	(L) values are constants. However, in your version you will need them to be 	
	variables. Clearly, as the bits per codeword value increases, so should the number
	of codewords value.

* The TST the author uses can grow dynamically, so it does not matter how large
	the dictionary will be. However, for the `expand()` method an array of String
	is used for the dictionary. Make sure this is large enough to accommodate the
	maximum possible number of codewords.

* Carefully trace what your code is doing as you modify it. You only have
	to write a few lines of code for this program, but it could still require a 	
	substantial amount of time to get to work properly. I recommend tracing the
	portions of the code you plan to modify on paper. Another idea would be to have
	an extra output file for each of the `compress()` and `expand()` methods to
	output any trace code. Printing out (codeword, string) pairs in the iterations
	just before and after a bit change or reset is done can help you a lot to
	synchronize your code properly.

* Consider the notes in `LZW.java` (and `TST.java`) concerning the speed of the
	`substring()` function. In order to run your experiments faster, you may want
	to edit LZW.java and TST.java to remove all calls to `substring()`.
	There is no penalty for continuing to use `substring()` for this assignment, but
	you will experience noticeably slow performance on large files.
