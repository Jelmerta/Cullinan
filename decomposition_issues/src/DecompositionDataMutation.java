// For all data that is passed:
// If previously, the data passed to the API call is used again we also need to make sure to return this data and assign the value in the calling statement

class DataMutator {
    boolean isEverythingGood = false;

    public boolean changeData() {
        changeGoodness(isEverythingGood); // Original
        // 2. GoodnessClient.changeGoodness(isEverythingGood); // New call: Not sufficient, isEverythingGood is not changed without an extra change
        return isEverythingGood;
    }

    // This function changes the data in the class
    // While the API also might change the data in its service, it is not automatically returned.
    // If a function changes state of a class variable, we need to make sure that the service also returns this value and the caller assigns it
    private void changeGoodness(boolean isEverythingGood) {
        isEverythingGood = !isEverythingGood;
    }
}