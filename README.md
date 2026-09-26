## Checkpoint 2: Computation

### Count the Number of Prime Numbers Below the Input

The computation will count how many prime numbers are smaller than a given positive integer.

The input must be a single positive integer greater than 0 and less than `Integer.MAX_VALUE`.

For each input number, the computation checks the numbers below it and determines which numbers are prime. It then returns the total number of prime numbers found.

For example, if the input is `10`, the prime numbers below 10 are:

`2, 3, 5, 7`

Therefore, the result is `4`.

If the input is `20`, the prime numbers below 20 are:

`2, 3, 5, 7, 11, 13, 17, 19`

Therefore, the result is `8`.

### Sample Input and Output

| Input | Prime Numbers Below Input                | Output |
| ----- | ---------------------------------------- | -----: |
| 10    | 2, 3, 5, 7                               |      4 |
| 20    | 2, 3, 5, 7, 11, 13, 17, 19               |      8 |
| 100   | 2, 3, 5, 7, 11, 13, 17, 19, 23, ... , 97 |     25 |

### System Diagram

![System Diagram](checkpoint2.png)
