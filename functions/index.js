const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
//admin.initializeApp();
admin.initializeApp(functions.config().firebase);

//const sendgridemail = require('@sendgrid/mail');
//const MY_SENDGRID_API_KEY = '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'
//sendgridemail.setApiKey(MY_SENDGRID_API_KEY);

exports.randomizeUsers = functions.https.onRequest((req, res) => {
    const sgMail = require('@sendgrid/mail');
    sgMail.setApiKey('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');

//     const useremail = req.query.email;
     const userprice = req.query.price;

    const msg = {
      to: 'aryamandas@ucsb.edu',
      from: 'efreilafert@gmail.com',
      subject: 'Your most recent Costco trip',
      html: 'You owe ' + userprice + ' dollars.',
    };
    sgMail.send(msg);

});

//
//exports.payEmail = functions.firestore.document('customers/{customerId}/payments/{paymentId}').onCreate(event => {
//    const customerId = event.params.customerId;
//    const fsdb = admin.firestore()
//    return fsdb.collection('customers').doc(customerId).get().then(doc => {
//        const customerdata = doc.data()
//        const msgbody = {
//            to: customerdata.email,
//            from: 'auto-reply@xyzshopping.com',
//            subject:  'Payment Success - xyzshopping.com',
//            templateId: 'and easy to do anywhere, even with Node.js',
//            substitutionWrappers: ['{{', '}}'],
//            substitutions: {
//              name: customerdata.displayName
//            }
//        };
//        return payEmail.send(msgbody)}).then(() => console.log('payment mail sent success')).catch(err => console.log(err))
//});


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
