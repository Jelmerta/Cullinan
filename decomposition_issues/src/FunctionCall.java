// This file explains a possible problem with microservice decomposition not working and requiring human expertise
// When a seam is found, and therefore an API call needs to be made, we have to look at the information that should be passed between the two parts.
// One issue can be that an object is passed containing a function call that is executed.
// I believe some of the identification algorithms (implicitly?) take care of this by splitting classes up between microservices.
// When a class only lives in one domain, it would seem that a seam cannot be found there
// If the identification algorithm does not take care of this, it would be very difficult to automatically transform the code.

// (Obviously, in this example, the responsibilities are all quite bad and coupled, creating this problem)
public class FunctionCall {
    public static void main(String[] args) {
        CargoManager cargoManager = new CargoManager();
        cargoManager.manageCargo();
    }
}

class CargoManager {
    MailManager mailManager = new MailManager();

    public void manageCargo() {
        MailFormatter cargoManageMailFormatter = new MailFormatter();
        // Do Cargo stuff and then send a mail
        // If a seam is found for a Mail Service, but we pass an object containing functions, not just data, then we cannot do this over an API call without first restructuring the code and splitting responsibilities.
        mailManager.sendMail(cargoManageMailFormatter);
    }
}

// Contains information about how the mail should be formatted
class MailFormatter {
    public Mail formatMail() {
        // Performs some actions resulting in a mail
        return new Mail();
    }
}

class Mail {

}

class MailManager {
    // MailFormatter is not just data, and therefore cannot easily be changed to an API call.
    public void sendMail(MailFormatter mailFormatter) {
        // Code to send mail
        mailFormatter.formatMail();
    }
}
