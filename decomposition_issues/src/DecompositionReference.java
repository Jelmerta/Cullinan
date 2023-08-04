// 1. Java does not pass by reference, but languages that do may struggle with decomposition
// If the data from the reference is used, it is not sufficient to pass this through an API, as the data cannot (hopefully) be accessed by another service

public class DecompositionReference {
    public static void main(String[] args) {

    }
}

class MailManagerTwo {
    public void sendMail(MailTwo referenceToMail) {

    }
}

class MailTwo {

}
